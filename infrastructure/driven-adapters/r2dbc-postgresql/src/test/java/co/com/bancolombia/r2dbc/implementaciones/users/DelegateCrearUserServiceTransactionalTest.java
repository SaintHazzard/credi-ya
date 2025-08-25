package co.com.bancolombia.r2dbc.implementaciones.users;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import co.com.bancolombia.model.common.CrearStrategy;
import co.com.bancolombia.model.common.ReactiveTx;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.CrearStrategyEnum;
import co.com.bancolombia.model.user.gateways.StrategyUserFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Test enfocado en probar el comportamiento transaccional
 * cuando ocurren errores durante la creación de usuarios.
 */
@ExtendWith(MockitoExtension.class)
public class DelegateCrearUserServiceTransactionalTest {

  @Mock
  private StrategyUserFactory factory;

  @Mock
  private ReactiveTx reactiveTx;

  @Mock
  private CrearStrategy<User> simpleStrategy;

  /**
   * Este test verifica que las transacciones se comporten atómicamente.
   * Cuando la creación del segundo usuario falla, la primera operación
   * debería revertirse (rollback).
   */
  @Test
  void createTwoUsers_whenSecondUserFails_shouldRollbackFirstUser() {
    // Arrange
    User user1 = User.builder().names("John").lastname("Doe").email("john@example.com").build();
    User user2 = User.builder().names("Jane").lastname("Smith").email("jane@example.com").build();

    User savedUser1 = User.builder().id("1").names("John").lastname("Doe").email("john@example.com").build();

    // Lista para rastrear qué usuarios fueron realmente creados
    List<User> persistedUsers = new ArrayList<>();

    // Flag para rastrear si la transacción se completó o se hizo rollback
    AtomicBoolean transactionCommitted = new AtomicBoolean(false);

    // Simulamos una operación exitosa para el primer usuario
    when(simpleStrategy.create(user1)).thenAnswer(invocation -> {
      persistedUsers.add(user1);
      return Mono.just(savedUser1);
    });

    // Simulamos un error para el segundo usuario
    RuntimeException userCreationException = new RuntimeException("Error al crear el segundo usuario");
    when(simpleStrategy.create(user2)).thenAnswer(invocation -> Mono.error(userCreationException));

    when(factory.getStrategy(CrearStrategyEnum.SIMPLE)).thenReturn(simpleStrategy);

    // Simulamos el comportamiento transaccional de reactiveTx
    when(reactiveTx.write(any())).thenAnswer((Answer<Mono<Void>>) invocation -> {
      Supplier<Mono<Void>> operation = invocation.getArgument(0);

      return operation.get()
          .doOnSuccess(v -> transactionCommitted.set(true))
          .doOnError(e -> {
            // Simulamos el rollback limpiando la lista de usuarios persistidos
            persistedUsers.clear();
          });
    });

    // Act & Assert
    DelegateCrearUserService service = new DelegateCrearUserService(factory, reactiveTx);

    StepVerifier.create(service.createTwoUsers(user1, user2))
        .expectErrorMatches(error -> error instanceof RuntimeException &&
            error.getMessage().equals("Error al crear el segundo usuario"))
        .verify();

    // Verificaciones

    // 1. Verificar que la transacción no se confirmó
    assert !transactionCommitted.get() : "La transacción no debería haberse confirmado";

    // 2. Verificar que el primer usuario se agregó inicialmente pero después del
    // rollback
    // la lista debería estar vacía
    assert persistedUsers.isEmpty() : "Después del rollback, ningún usuario debería persistir";

    // 3. Verificar que se llamó al método write de reactiveTx
    verify(simpleStrategy).create(user1);
    verify(simpleStrategy).create(user2);
    verify(reactiveTx).write(any());
    verifyNoMoreInteractions(simpleStrategy);
  }

  /**
   * Este test verifica que cuando hay múltiples usuarios y uno falla,
   * todos los usuarios creados anteriormente se revierten.
   */
  @Test
  void createMultipleUsers_whenOneUserFails_shouldRollbackAllPreviousUsers() {
    // Arrange
    var user1 = User.builder().names("User1").lastname("Test").email("user1@example.com").build();
    var user2 = User.builder().names("User2").lastname("Test").email("user2@example.com").build();
    var user3 = User.builder().names("User3").lastname("Test").email("user3@example.com").build();

    var users = Arrays.asList(user1, user2, user3);

    // Rastreo de "persistencia" simulada
    List<User> persistedUsers = new ArrayList<>();
    AtomicBoolean transactionCommitted = new AtomicBoolean(false);

    // Mocks de estrategia: u1 y u2 OK, u3 falla
    when(simpleStrategy.create(user1)).thenAnswer(__ -> {
      persistedUsers.add(user1);
      return Mono.just(user1.toBuilder().id("1").build());
    });

    when(simpleStrategy.create(user2)).thenAnswer(__ -> {
      persistedUsers.add(user2);
      return Mono.just(user2.toBuilder().id("2").build());
    });

    RuntimeException failure = new RuntimeException("Error al crear el tercer usuario");
    when(simpleStrategy.create(user3)).thenReturn(Mono.error(failure));

    when(factory.getStrategy(CrearStrategyEnum.SIMPLE)).thenReturn(simpleStrategy);

    // Simular transacción: diferir ejecución, marcar "commit" en success y
    // "rollback" en error
    when(reactiveTx.write(any())).thenAnswer((Answer<Mono<Void>>) invocation -> {
      Supplier<Mono<Void>> operation = invocation.getArgument(0);
      return Mono.defer(operation::get) // <- importante: defer
          .doOnSuccess(v -> transactionCommitted.set(true))
          .doOnError(e -> persistedUsers.clear()); // "rollback" simulado
    });

    // Act
    var service = new DelegateCrearUserService(factory, reactiveTx);

    // Assert
    StepVerifier.create(service.createMultipleUsers(users))
        .expectErrorMessage("Error al crear el tercer usuario")
        .verify();

    // 1) Después del rollback no debe quedar nada "persistido"
    assertTrue(persistedUsers.isEmpty(), "Después del rollback, ningún usuario debería persistir");

    // 2) Verificar orden estricto (secuencial). Si alguien cambia a flatMap, este
    // check fallará.
    InOrder inOrder = inOrder(simpleStrategy);
    inOrder.verify(simpleStrategy).create(user1);
    inOrder.verify(simpleStrategy).create(user2);
    inOrder.verify(simpleStrategy).create(user3);
    verifyNoMoreInteractions(simpleStrategy);

    // 3) Se ejecutó dentro de una única transacción
    verify(reactiveTx).write(any());

    // 4) La transacción NO debe marcarse como exitosa en el flujo de error
    assertFalse(transactionCommitted.get(), "La transacción no debería haberse confirmado");
  }

  @Test
  void createMultipleUsers_allOk_shouldCommit() {
    User u1 = User.builder().names("U1").lastname("T").email("u1@x.com").build();
    User u2 = User.builder().names("U2").lastname("T").email("u2@x.com").build();
    List<User> users = Arrays.asList(u1, u2);

    List<User> persistedUsers = new ArrayList<>();
    AtomicBoolean committed = new AtomicBoolean(false);

    when(factory.getStrategy(CrearStrategyEnum.SIMPLE)).thenReturn(simpleStrategy);
    when(simpleStrategy.create(u1)).thenAnswer(__ -> {
      persistedUsers.add(u1);
      return Mono.just(u1.toBuilder().id("1").build());
    });
    when(simpleStrategy.create(u2)).thenAnswer(__ -> {
      persistedUsers.add(u2);
      return Mono.just(u2.toBuilder().id("2").build());
    });

    when(reactiveTx.write(any())).thenAnswer((Answer<Mono<Void>>) inv -> {
      Supplier<Mono<Void>> op = inv.getArgument(0);
      return Mono.defer(op::get).doOnSuccess(v -> committed.set(true));
    });

    DelegateCrearUserService s = new DelegateCrearUserService(factory, reactiveTx);

    StepVerifier.create(s.createMultipleUsers(users)).verifyComplete();

    assertTrue(committed.get(), "Debe comprometer la transacción");
    assertEquals(Arrays.asList(u1, u2), persistedUsers);
  }

}

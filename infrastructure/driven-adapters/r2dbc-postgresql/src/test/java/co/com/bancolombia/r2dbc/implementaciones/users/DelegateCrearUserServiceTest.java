package co.com.bancolombia.r2dbc.implementaciones.users;

import co.com.bancolombia.model.common.ReactiveTx;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.CrearStrategyEnum;
import co.com.bancolombia.model.user.gateways.StrategyUserFactory;
import co.com.bancolombia.model.common.CrearStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DelegateCrearUserServiceTest {

  @Mock
  private StrategyUserFactory factory;

  @Mock
  private ReactiveTx reactiveTx;

  @Mock
  private CrearStrategy<User> simpleStrategy;

  @Mock
  private CrearStrategy<User> resilienteStrategy;

  private DelegateCrearUserService delegateService;

  @BeforeEach
  void setUp() {
    delegateService = new DelegateCrearUserService(factory, reactiveTx);

    // Configuramos los mocks en el método de prueba específico, no aquí
  }

  @Test
  void create_withShortName_shouldUseSimpleStrategy() {
    // Arrange
    User user = User.builder()
        .names("John") // Nombre corto, menos de 25 caracteres
        .lastname("Doe")
        .email("john.doe@example.com")
        .build();

    User savedUser = User.builder()
        .id("123")
        .names("John")
        .lastname("Doe")
        .email("john.doe@example.com")
        .build();

    when(factory.getStrategy(CrearStrategyEnum.SIMPLE)).thenReturn(simpleStrategy);
    when(simpleStrategy.create(any(User.class))).thenReturn(Mono.just(savedUser));

    // Act & Assert
    StepVerifier.create(delegateService.create(user))
        .expectNext(savedUser)
        .verifyComplete();

    verify(factory).getStrategy(CrearStrategyEnum.SIMPLE);
    verify(simpleStrategy).create(any(User.class));
    verifyNoInteractions(resilienteStrategy);
  }

  @Test
  void create_withLongName_shouldUseResilienteStrategy() {
    // Arrange
    User user = User.builder()
        .names("This is a very long name that has more than 25 characters") // Nombre largo, más de 25 caracteres
        .lastname("Doe")
        .email("john.doe@example.com")
        .build();

    User savedUser = User.builder()
        .id("123")
        .names("This is a very long name that has more than 25 characters")
        .lastname("Doe")
        .email("john.doe@example.com")
        .build();

    when(factory.getStrategy(CrearStrategyEnum.RESILIENTE)).thenReturn(resilienteStrategy);
    when(resilienteStrategy.create(any(User.class))).thenReturn(Mono.just(savedUser));

    // Act & Assert
    StepVerifier.create(delegateService.create(user))
        .expectNext(savedUser)
        .verifyComplete();

    verify(factory).getStrategy(CrearStrategyEnum.RESILIENTE);
    verify(resilienteStrategy).create(any(User.class));
    verifyNoInteractions(simpleStrategy);
  }

  @Test
  void createUserSimple_shouldUseSimpleStrategy() {
    // Arrange
    User user = User.builder()
        .names("John")
        .lastname("Doe")
        .email("john.doe@example.com")
        .build();

    User savedUser = User.builder()
        .id("123")
        .names("John")
        .lastname("Doe")
        .email("john.doe@example.com")
        .build();

    when(factory.getStrategy(CrearStrategyEnum.SIMPLE)).thenReturn(simpleStrategy);
    when(simpleStrategy.create(any(User.class))).thenReturn(Mono.just(savedUser));

    // Act & Assert
    StepVerifier.create(delegateService.createUserSimple(user))
        .expectNext(savedUser)
        .verifyComplete();

    verify(factory).getStrategy(CrearStrategyEnum.SIMPLE);
    verify(simpleStrategy).create(any(User.class));
  }

  @Test
  void createUserWithResilience_shouldUseResilienteStrategy() {
    // Arrange
    User user = User.builder()
        .names("John")
        .lastname("Doe")
        .email("john.doe@example.com")
        .build();

    User savedUser = User.builder()
        .id("123")
        .names("John")
        .lastname("Doe")
        .email("john.doe@example.com")
        .build();

    when(factory.getStrategy(CrearStrategyEnum.RESILIENTE)).thenReturn(resilienteStrategy);
    when(resilienteStrategy.create(any(User.class))).thenReturn(Mono.just(savedUser));

    // Act & Assert
    StepVerifier.create(delegateService.createUserWithResilience(user))
        .expectNext(savedUser)
        .verifyComplete();

    verify(factory).getStrategy(CrearStrategyEnum.RESILIENTE);
    verify(resilienteStrategy).create(any(User.class));
  }

  @Test
  void createUserWithStrategy_shouldUseSpecifiedStrategy() {
    // Arrange
    User user = User.builder()
        .names("John")
        .lastname("Doe")
        .email("john.doe@example.com")
        .build();

    User savedUser = User.builder()
        .id("123")
        .names("John")
        .lastname("Doe")
        .email("john.doe@example.com")
        .build();

    when(factory.getStrategy(CrearStrategyEnum.SIMPLE)).thenReturn(simpleStrategy);
    when(simpleStrategy.create(any(User.class))).thenReturn(Mono.just(savedUser));

    // Act & Assert
    StepVerifier.create(delegateService.createUserWithStrategy(user, CrearStrategyEnum.SIMPLE))
        .expectNext(savedUser)
        .verifyComplete();

    verify(factory).getStrategy(CrearStrategyEnum.SIMPLE);
    verify(simpleStrategy).create(any(User.class));
  }

  @Test
  void createMultipleUsers_shouldCreateAllUsersInTransaction() {
    // Arrange
    User user1 = User.builder().names("John").lastname("Doe").build();
    User user2 = User.builder().names("Jane").lastname("Doe").build();

    List<User> users = Arrays.asList(user1, user2);

    when(factory.getStrategy(CrearStrategyEnum.SIMPLE)).thenReturn(simpleStrategy);
    when(simpleStrategy.create(any(User.class))).thenReturn(
        Mono.just(User.builder().id("1").names("John").lastname("Doe").build()),
        Mono.just(User.builder().id("2").names("Jane").lastname("Doe").build()));

    // Mock the transaction behavior
    when(reactiveTx.write(any())).thenAnswer(invocation -> {
      Supplier<Mono<Void>> supplier = invocation.getArgument(0);
      return supplier.get();
    });

    // Act & Assert
    StepVerifier.create(delegateService.createMultipleUsers(users))
        .verifyComplete();

    verify(reactiveTx).write(any());
    verify(factory, times(2)).getStrategy(CrearStrategyEnum.SIMPLE);
    verify(simpleStrategy, times(2)).create(any(User.class));
  }

  @Test
  void createTwoUsers_shouldCreateBothUsersInTransaction() {
    // Arrange
    User user1 = User.builder().names("John").lastname("Doe").build();
    User user2 = User.builder().names("Jane").lastname("Doe").build();

    when(factory.getStrategy(CrearStrategyEnum.SIMPLE)).thenReturn(simpleStrategy);
    when(simpleStrategy.create(any(User.class))).thenReturn(
        Mono.just(User.builder().id("1").names("John").lastname("Doe").build()),
        Mono.just(User.builder().id("2").names("Jane").lastname("Doe").build()));

    // Mock the transaction behavior
    when(reactiveTx.write(any())).thenAnswer(invocation -> {
      Supplier<Mono<Void>> supplier = invocation.getArgument(0);
      return supplier.get();
    });

    // Act & Assert
    StepVerifier.create(delegateService.createTwoUsers(user1, user2))
        .verifyComplete();

    verify(reactiveTx).write(any());
    verify(factory, times(2)).getStrategy(CrearStrategyEnum.SIMPLE);
    verify(simpleStrategy, times(2)).create(any(User.class));
  }

  @Test
  void createTwoUsers_withDuplicateEmail_shouldFailTransactionAndNotCreateAny() {
    // Arrange
    String duplicateEmail = "same.email@example.com";

    User user1 = User.builder()
        .names("John")
        .lastname("Doe")
        .email(duplicateEmail)
        .build();

    User user2 = User.builder()
        .names("Jane")
        .lastname("Smith")
        .email(duplicateEmail) // Mismo email que user1
        .build();

    when(factory.getStrategy(CrearStrategyEnum.SIMPLE)).thenReturn(simpleStrategy);

    // Simular que la primera creación es exitosa
    when(simpleStrategy.create(any(User.class))).thenReturn(
        Mono.just(user1.toBuilder().id("1").build()),
        Mono.error(new RuntimeException("Duplicate email constraint violation")));

    // Mock the transaction behavior - propagar el error
    when(reactiveTx.write(any())).thenAnswer(invocation -> {
      Supplier<Mono<Void>> supplier = invocation.getArgument(0);
      return supplier.get(); // Este Mono contendrá el error
    });

    // Act & Assert
    StepVerifier.create(delegateService.createTwoUsers(user1, user2))
        .expectErrorMatches(error -> error instanceof RuntimeException &&
            error.getMessage().contains("Duplicate email constraint violation"))
        .verify();

    // Verificar que se llamó a la transacción
    verify(reactiveTx).write(any());

    // Verificar que se intentó crear ambos usuarios
    verify(factory, times(2)).getStrategy(CrearStrategyEnum.SIMPLE);
    verify(simpleStrategy, times(2)).create(any(User.class));
  }
}

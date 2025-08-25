package co.com.bancolombia.r2dbc.implementaciones.users;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        
        // 2. Verificar que el primer usuario se agregó inicialmente pero después del rollback
        // la lista debería estar vacía
        assert persistedUsers.isEmpty() : "Después del rollback, ningún usuario debería persistir";
        
        // 3. Verificar que se llamó al método write de reactiveTx
        verify(reactiveTx).write(any());
        
        // 4. Verificar que ambas estrategias se intentaron
        verify(factory, times(2)).getStrategy(CrearStrategyEnum.SIMPLE);
        verify(simpleStrategy).create(user1);
        verify(simpleStrategy).create(user2);
    }
    
    /**
     * Este test verifica que cuando hay múltiples usuarios y uno falla,
     * todos los usuarios creados anteriormente se revierten.
     */
    @Test
    void createMultipleUsers_whenOneUserFails_shouldRollbackAllPreviousUsers() {
        // Arrange
        User user1 = User.builder().names("User1").lastname("Test").email("user1@example.com").build();
        User user2 = User.builder().names("User2").lastname("Test").email("user2@example.com").build();
        User user3 = User.builder().names("User3").lastname("Test").email("user3@example.com").build();
        
        List<User> users = Arrays.asList(user1, user2, user3);
        
        // Lista para rastrear qué usuarios fueron realmente creados
        List<User> persistedUsers = new ArrayList<>();
        
        // Configuramos las respuestas del mock: user1 y user2 exitosos, user3 falla
        when(simpleStrategy.create(user1)).thenAnswer(invocation -> {
            persistedUsers.add(user1);
            return Mono.just(user1.toBuilder().id("1").build());
        });
        
        when(simpleStrategy.create(user2)).thenAnswer(invocation -> {
            persistedUsers.add(user2);
            return Mono.just(user2.toBuilder().id("2").build());
        });
        
        RuntimeException failureException = new RuntimeException("Error al crear el tercer usuario");
        when(simpleStrategy.create(user3)).thenReturn(Mono.error(failureException));
        
        when(factory.getStrategy(CrearStrategyEnum.SIMPLE)).thenReturn(simpleStrategy);
        
        // Simulamos el comportamiento transaccional
        when(reactiveTx.write(any())).thenAnswer((Answer<Mono<Void>>) invocation -> {
            Supplier<Mono<Void>> operation = invocation.getArgument(0);
            
            return operation.get()
                .doOnError(e -> {
                    // Simulamos el rollback limpiando la lista de usuarios persistidos
                    persistedUsers.clear();
                });
        });
        
        // Act & Assert
        DelegateCrearUserService service = new DelegateCrearUserService(factory, reactiveTx);
        
        StepVerifier.create(service.createMultipleUsers(users))
            .expectErrorMatches(error -> error.getMessage().equals("Error al crear el tercer usuario"))
            .verify();
        
        // Verificaciones
        
        // 1. Verificar que después del rollback, ningún usuario persiste
        assert persistedUsers.isEmpty() : "Después del rollback, ningún usuario debería persistir";
        
        // 2. Verificar que todos los usuarios se intentaron crear
        verify(simpleStrategy).create(user1);
        verify(simpleStrategy).create(user2);
        verify(simpleStrategy).create(user3);
    }
}

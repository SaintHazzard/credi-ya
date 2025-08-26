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
import reactor.core.publisher.Flux;
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
  void createMultipleUsers_shouldCreateAllUsersInTransaction() {
    // Arrange
    User user1 = User.builder().names("John").lastname("Doe").build();
    User user2 = User.builder().names("Jane").lastname("Doe").build();

    List<User> users = Arrays.asList(user1, user2);

    when(factory.getStrategy(CrearStrategyEnum.SIMPLE)).thenReturn(simpleStrategy);
    
    // Configuramos las respuestas para las dos llamadas a create
    when(simpleStrategy.create(any(User.class)))
        .thenReturn(Mono.just(User.builder().id("1").names("John").lastname("Doe").build()))
        .thenReturn(Mono.just(User.builder().id("2").names("Jane").lastname("Doe").build()));

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
}

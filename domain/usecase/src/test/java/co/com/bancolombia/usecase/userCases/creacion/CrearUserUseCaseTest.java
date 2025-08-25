package co.com.bancolombia.usecase.userCases.creacion;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.CrearStrategyEnum;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private CrearUserUseCase crearUserUseCase;

    @BeforeEach
    void setUp() {
        crearUserUseCase = new CrearUserUseCase(userRepository);
    }

    @Test
    void create_shouldSaveUser() {
        // Arrange
        User user = User.builder()
                .id("123")
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
        
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // Act & Assert
        StepVerifier.create(crearUserUseCase.create(user))
                .expectNext(savedUser)
                .verifyComplete();
    }

    @Test
    void getType_shouldReturnSimpleStrategy() {
        // Act & Assert
        assertEquals(CrearStrategyEnum.SIMPLE, crearUserUseCase.getType());
    }
}

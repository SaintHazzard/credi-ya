package co.com.bancolombia.usecase.userCases.creacion;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.CrearStrategyEnum;
import co.com.bancolombia.model.user.gateways.PasswordEncoderPort;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.user.validator.UserValidatorPort;
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
    
    @Mock
    private UserValidatorPort userValidatorPort;
    
    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    private CrearUserUseCase crearUserUseCase;

    @BeforeEach
    void setUp() {
        crearUserUseCase = new CrearUserUseCase(userRepository, userValidatorPort, passwordEncoderPort);
    }

    @Test
    void create_shouldSaveUser() {
        // Arrange
        User user = User.builder()
                .id("123")
                .names("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();
        
        User validatedUser = User.builder()
                .id("123")
                .names("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();
        
        User savedUser = User.builder()
                .id("123")
                .names("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .build();
        
        when(userValidatorPort.validateUser(any(User.class))).thenReturn(Mono.just(validatedUser));
        when(passwordEncoderPort.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // Act & Assert
        StepVerifier.create(crearUserUseCase.create(user))
                .expectNext(savedUser)
                .verifyComplete();
    }
    
    @Test
    void create_whenValidationFails_shouldReturnError() {
        // Arrange
        User user = User.builder()
                .id("123")
                .names("John")
                .lastname("Doe")
                .email("invalid-email")
                .password("password123")
                .build();
        
        RuntimeException validationError = new RuntimeException("Validation error");
        when(userValidatorPort.validateUser(any(User.class))).thenReturn(Mono.error(validationError));
        // No necesitamos mockear el passwordEncoder porque la validación fallará antes
        
        // Act & Assert
        StepVerifier.create(crearUserUseCase.create(user))
                .expectErrorMatches(throwable -> throwable.equals(validationError))
                .verify();
    }

    @Test
    void getType_shouldReturnSimpleStrategy() {
        // Act & Assert
        assertEquals(CrearStrategyEnum.SIMPLE, crearUserUseCase.getType());
    }
}

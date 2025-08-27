package co.com.bancolombia.api;

import co.com.bancolombia.api.user.UserHandler;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.ManagementUserPort;
import co.com.bancolombia.model.user.gateways.UserCreationPort;
import co.com.bancolombia.r2dbc.dtos.user.UserDTO;
import co.com.bancolombia.r2dbc.entities.userentity.UserMapper;
import co.com.bancolombia.r2dbc.helper.utilities.ValidationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserHandlerTest {

  @Mock
  private UserCreationPort userCreationPort;

  @Mock
  private ManagementUserPort managementUserPort;

  @Mock
  private ValidationHandler validationHandler;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserHandler userHandler;

  @BeforeEach
  void setUp() {
    // Configuración común para los mocks
    when(validationHandler.validate(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
  }

  @Test
  void listenGetUserByIdShouldReturnUser() {
    // Arrange
    User user = createTestUser();
    UserDTO userDTO = createTestUserDTO();

    ServerRequest request = mock(ServerRequest.class);
    when(request.pathVariable("id")).thenReturn("123");

    when(managementUserPort.findUserById("123")).thenReturn(Mono.just(user));
    when(userMapper.toDto(user)).thenReturn(userDTO);

    // Act
    Mono<ServerResponse> response = userHandler.listenGetUser(request);

    // Assert
    StepVerifier.create(response)
        .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.OK))
        .verifyComplete();
  }

  @Test
  void listenCreateUserShouldCreateUser() {
    // Arrange
    User user = createTestUser();
    UserDTO userDTO = createTestUserDTO();

    ServerRequest request = mock(ServerRequest.class);
    when(request.bodyToMono(UserDTO.class)).thenReturn(Mono.just(userDTO));

    when(userMapper.toDomain(userDTO)).thenReturn(user);
    when(userCreationPort.create(user)).thenReturn(Mono.just(user));
    when(userMapper.toDto(user)).thenReturn(userDTO);

    // Act
    Mono<ServerResponse> response = userHandler.listenCreateUser(request);

    // Assert
    StepVerifier.create(response)
        .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.CREATED))
        .verifyComplete();
  }

  private User createTestUser() {
    return User.builder()
        .id("123")
        .names("Alejandro")
        .lastname("Pérez")
        .birthDate(LocalDate.of(1990, 1, 1))
        .address("Calle 123 #45-67")
        .phone("3001234567")
        .email("alejo@example.com")
        .salaryBase(BigDecimal.valueOf(3500000))
        .username("hazzy")
        .password("1324")
        .build();
  }

  private UserDTO createTestUserDTO() {
    UserDTO dto = new UserDTO();
    dto.setId("123");
    dto.setNames("Alejandro");
    dto.setLastname("Pérez");
    dto.setBirthDate(LocalDate.of(1990, 1, 1));
    dto.setAddress("Calle 123 #45-67");
    dto.setPhone("3001234567");
    dto.setEmail("alejo@example.com");
    dto.setSalaryBase(BigDecimal.valueOf(3500000));
    dto.setUsername("hazzy");
    dto.setPassword("1324");
    return dto;
  }
}
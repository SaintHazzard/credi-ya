package co.com.bancolombia.r2dbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.entities.userentity.UserEntity;
import co.com.bancolombia.r2dbc.entities.userentity.UserEntityRepository;
import co.com.bancolombia.r2dbc.entities.userentity.UserReactiveEntityRepositoryAdapater;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    private UserReactiveEntityRepositoryAdapater repositoryAdapter;

    @Mock
    private UserEntityRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Inicializa manualmente el repositoryAdapter con los mocks y la función de mapeo
        repositoryAdapter = new UserReactiveEntityRepositoryAdapater(repository, objectMapper);
    }

    @Test
    void mustFindValueById() {
        // Arrange
        String userId = "1";
        UserEntity userEntity = mock(UserEntity.class);
        User user = mock(User.class);
        
        // Configura los mocks
        when(repository.findById(userId)).thenReturn(Mono.just(userEntity));
        when(objectMapper.map(eq(userEntity), eq(User.class))).thenReturn(user);

        // Act
        Mono<User> result = repositoryAdapter.findById(userId);

        // Assert
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        // Arrange
        UserEntity userEntity = mock(UserEntity.class);
        User user = mock(User.class);
        
        // Configura los mocks
        when(repository.findAll()).thenReturn(Flux.just(userEntity));
        when(objectMapper.map(eq(userEntity), eq(User.class))).thenReturn(user);

        // Act
        Flux<User> result = repositoryAdapter.findAll();

        // Assert
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void mustFindByExample() {
        // Arrange
        UserEntity userEntity = mock(UserEntity.class);
        User user = mock(User.class);
        UserEntity exampleEntity = mock(UserEntity.class);
        
        // Mocks para trabajar con Example
        when(objectMapper.map(eq(user), eq(UserEntity.class))).thenReturn(exampleEntity);
        when(repository.findAll(any(Example.class))).thenReturn(Flux.just(userEntity));
        when(objectMapper.map(eq(userEntity), eq(User.class))).thenReturn(user);

        // Act
        Flux<User> result = repositoryAdapter.findByExample(user); 

        // Assert
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        // Arrange
        UserEntity userEntity = mock(UserEntity.class);
        User domainObject = mock(User.class);
        
        // Configura los mocks
        when(objectMapper.map(eq(domainObject), eq(UserEntity.class))).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.just(userEntity));
        when(objectMapper.map(eq(userEntity), eq(User.class))).thenReturn(domainObject);

        // Act
        Mono<User> result = repositoryAdapter.save(domainObject);

        // Assert
        StepVerifier.create(result)
                .expectNext(domainObject)
                .verifyComplete();
    }
}

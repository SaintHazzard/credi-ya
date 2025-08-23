package co.com.bancolombia.r2dbc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import co.com.bancolombia.mappers.UserMapper;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.entities.userentity.UserEntity;
import co.com.bancolombia.r2dbc.entities.userentity.UserEntityRepository;
import co.com.bancolombia.r2dbc.entities.userentity.UserReactiveEntityRepositoryAdapater;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @InjectMocks
    UserReactiveEntityRepositoryAdapater repositoryAdapter;

    @Mock
    UserEntityRepository repository;

    @Mock
    UserMapper mapper;

    @Test
    void mustFindValueById() {

        var userEntity = mock(UserEntity.class);
        when(repository.findById("1")).thenReturn(Mono.just(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(mock(User.class));

        Mono<User> result = repositoryAdapter.findById("1");

        StepVerifier.create(result)
                .expectNextMatches(value -> value instanceof User)
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        UserEntity userEntity = org.mockito.Mockito.mock(UserEntity.class);
        when(repository.findAll()).thenReturn(Flux.just(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(mock(User.class));

        Flux<User> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value instanceof User)
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {
        UserEntity userEntity = mock(UserEntity.class);
        User user = mock(User.class);
        @SuppressWarnings("unchecked")
        Example<UserEntity> example = (Example<UserEntity>) any(Example.class);
        when(repository.findAll(example)).thenReturn(Flux.just(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(user);

        Flux<User> result = repositoryAdapter.findByExample(user); 

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        UserEntity userEntity = mock(UserEntity.class);
        User domainObject = new User("123", "John Doe", "Paquito", "john.doe@example.com");
        when(mapper.toEntity(domainObject)).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.just(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(domainObject);

        Mono<User> result = repositoryAdapter.save(domainObject);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(domainObject))
                .verifyComplete();
    }
}

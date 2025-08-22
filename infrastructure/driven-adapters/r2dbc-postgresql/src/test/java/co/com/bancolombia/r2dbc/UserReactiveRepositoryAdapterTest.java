package co.com.bancolombia.r2dbc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {
    // TODO: change four you own tests

    @InjectMocks
    UserReactiveEntityRepositoryAdapater repositoryAdapter;

    @Mock
    UserEntityRepository repository;

    @Mock
    ObjectMapper mapper;

    @Test
    void mustFindValueById() {

        var userEntity = org.mockito.Mockito.mock(UserEntity.class);
        when(repository.findById("1")).thenReturn(Mono.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(org.mockito.Mockito.mock(User.class));

        Mono<User> result = repositoryAdapter.findById("1");

        StepVerifier.create(result)
                .expectNextMatches(value -> value instanceof User)
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        UserEntity userEntity = org.mockito.Mockito.mock(UserEntity.class);
        when(repository.findAll()).thenReturn(Flux.just(userEntity));
        when(mapper.map(userEntity, Object.class)).thenReturn("test");

        Flux<Object> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals("test"))
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {
        when(repository.findAll(any(Example.class))).thenReturn(Flux.just("test"));
        when(mapper.map("test", Object.class)).thenReturn("test");

        Flux<Object> result = repositoryAdapter.findByExample("test");

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals("test"))
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        when(repository.save("test")).thenReturn(Mono.just("test"));
        when(mapper.map("test", Object.class)).thenReturn("test");

        Mono<Object> result = repositoryAdapter.save("test");

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals("test"))
                .verifyComplete();
    }
}

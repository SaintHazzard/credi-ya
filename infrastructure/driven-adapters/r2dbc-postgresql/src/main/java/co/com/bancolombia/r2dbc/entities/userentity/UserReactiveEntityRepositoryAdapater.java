package co.com.bancolombia.r2dbc.entities.userentity;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import reactor.core.publisher.Mono;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserReactiveEntityRepositoryAdapater extends ReactiveAdapterOperations<
    User/* change for domain model */,
    UserEntity/* change for adapter model */,
    String,
    UserEntityRepository
> implements UserRepository {
    public UserReactiveEntityRepositoryAdapater(UserEntityRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, User.class/* change for domain model */));
        
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
            .map(entity -> mapper.map(entity, User.class));
    }
    
    @Override
    public Mono<User> findByUsername(String username) {
        return repository.findByUsername(username)
            .map(entity -> mapper.map(entity, User.class));
    }
}

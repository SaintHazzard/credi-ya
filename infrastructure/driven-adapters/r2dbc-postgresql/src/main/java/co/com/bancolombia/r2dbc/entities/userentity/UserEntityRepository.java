package co.com.bancolombia.r2dbc.entities.userentity;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserEntityRepository extends ReactiveCrudRepository<UserEntity, String>, ReactiveQueryByExampleExecutor<UserEntity> {

}

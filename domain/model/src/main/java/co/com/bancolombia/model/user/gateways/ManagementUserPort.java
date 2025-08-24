package co.com.bancolombia.model.user.gateways;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManagementUserPort {
  
  public Mono<User> findUserById(String id);

  public Flux<User> findAllUsers();
}

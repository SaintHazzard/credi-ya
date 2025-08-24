package co.com.bancolombia.usecase.creacionuser;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.ManagementUserPort;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ManagementUserUseCase implements ManagementUserPort {

  private final UserRepository userRepository;

  public Mono<User> findUserById(String id) {
    return userRepository.findById(id);
  }

  public Flux<User> findAllUsers() {
    return userRepository.findAll();
  }
}

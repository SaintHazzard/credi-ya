package co.com.bancolombia.usecase.userCases;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ActualizarUserUseCase {
  private final UserRepository userRepository;


  public Mono<User> updateUser(User user) {
    if (user.getId() == null) {
      return Mono.error(new IllegalArgumentException("User ID must not be null"));
    }
    return userRepository.save(user);
  }
}

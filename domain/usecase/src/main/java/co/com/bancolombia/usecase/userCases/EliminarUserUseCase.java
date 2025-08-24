package co.com.bancolombia.usecase.userCases;

import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class EliminarUserUseCase {
  private final UserRepository userRepository;


  public Mono<Void> deleteUser(String userId) {
    if (userId == null) {
      return Mono.error(new IllegalArgumentException("User ID must not be null"));
    }
    return userRepository.deleteById(userId);
  }
}

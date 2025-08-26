package co.com.bancolombia.usecase.userCases;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.PasswordEncoderPort;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthUseCase {
  private final UserRepository userRepository;

  private final PasswordEncoderPort passwordEncoder;


  public Mono<User> authenticateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")));
    }
}

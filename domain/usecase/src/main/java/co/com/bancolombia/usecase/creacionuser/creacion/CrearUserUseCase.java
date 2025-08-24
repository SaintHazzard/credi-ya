package co.com.bancolombia.usecase.creacionuser.creacion;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;



@RequiredArgsConstructor
public class CrearUserUseCase implements CrearUsuarioStrategy {

  private final UserRepository userRepository;

  public Mono<User> createUser(User user) {
    return userRepository.save(user);
  }

  @Override
  public CrearUserStrategyEnum getType() {
    return CrearUserStrategyEnum.SIMPLE;
  }

  
}

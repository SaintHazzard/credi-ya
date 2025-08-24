package co.com.bancolombia.usecase.userCases.creacion;

import co.com.bancolombia.model.common.CrearStrategy;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.CrearStrategyEnum;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;



@RequiredArgsConstructor
public class CrearUserUseCase implements CrearStrategy<User> {

  private final UserRepository userRepository;

  @Override
  public Mono<User> create(User user) {
    return userRepository.save(user);
  }

  @Override
  public CrearStrategyEnum getType() {
    return CrearStrategyEnum.SIMPLE;
  }

  
}

package co.com.bancolombia.usecase.userCases.creacion;

import co.com.bancolombia.model.common.CrearStrategy;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.CrearStrategyEnum;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.user.validator.UserValidatorPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;



@RequiredArgsConstructor
public class CrearUserUseCase implements CrearStrategy<User> {

  private final UserRepository userRepository;
  private final UserValidatorPort userValidatorPort;

  @Override
  public Mono<User> create(User user) {
    return userValidatorPort.validateUser(user)
        .flatMap(validatedUser -> userRepository.save(validatedUser));
  }

  @Override
  public CrearStrategyEnum getType() {
    return CrearStrategyEnum.SIMPLE;
  }

  
}

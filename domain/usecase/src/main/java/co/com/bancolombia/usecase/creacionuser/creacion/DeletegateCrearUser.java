package co.com.bancolombia.usecase.creacionuser.creacion;

import java.util.Map;

import co.com.bancolombia.model.user.User;

import reactor.core.publisher.Mono;

public class DeletegateCrearUser {

  private final Map<CrearUserStrategyEnum, CrearUsuarioStrategy> strategies;

  public DeletegateCrearUser(Map<CrearUserStrategyEnum, CrearUsuarioStrategy> strategies) {
    this.strategies = strategies;
  }

  public Mono<User> createUser(User user) {
    if (user.getNames().length() > 25) {
      return strategies.get(CrearUserStrategyEnum.RESILIENTE).createUser(user);
    }
    return strategies.get(CrearUserStrategyEnum.SIMPLE).createUser(user);
  }

}

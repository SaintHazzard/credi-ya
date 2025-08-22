package co.com.bancolombia.usecase.creacionuser.creacion;

import java.util.Map;

import co.com.bancolombia.model.user.User;

import reactor.core.publisher.Mono;

public class DeletegateCrearUser {

  private final Map<CrearUserStrategy, CrearUsuarioStrategy> strategies;

  public DeletegateCrearUser(Map<CrearUserStrategy, CrearUsuarioStrategy> strategies) {
    this.strategies = strategies;
  }

  public Mono<User> createUser(User user) {
    if (user.getNombres().length() > 25) {
      return strategies.get(CrearUserStrategy.RESILIENTE).createUser(user);
    }
    return strategies.get(CrearUserStrategy.SIMPLE).createUser(user);
  }

}

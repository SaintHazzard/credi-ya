package co.com.bancolombia.usecase.creacionuser.creacion;

import co.com.bancolombia.model.common.ResilienceService;
import co.com.bancolombia.model.user.User;

import co.com.bancolombia.model.user.gateways.CrearUserStrategyEnum;
import co.com.bancolombia.model.user.gateways.CrearUsuarioStrategy;
import co.com.bancolombia.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;

public class CrearUserCaseResilience implements CrearUsuarioStrategy {
  private final UserRepository repo;
  private final ResilienceService resilience;

  public CrearUserCaseResilience(UserRepository repo, ResilienceService resilience) {
    this.repo = repo;
    this.resilience = resilience;
  }

  @Override
  public Mono<User> createUser(User user) {
    return resilience.executeWithResilience(() -> Mono.defer(() -> {
      boolean fail = Math.random() > 0.4;
      if (fail) {
        return Mono.error(new RuntimeException("Simulated random failure 🚨"));
      }
      return repo.save(user);
    }));
  }


  @Override
  public CrearUserStrategyEnum getType() {
    return CrearUserStrategyEnum.RESILIENTE;
  }
}

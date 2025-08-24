package co.com.bancolombia.usecase.creacionuser.creacion;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.common.ReactiveTx;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.creacionuser.ResilienceService;
import reactor.core.publisher.Mono;

public class CrearUserCaseResilience implements CrearUsuarioStrategy {
  private final UserRepository repo;
  private final ResilienceService resilience;
  private final ReactiveTx tx;

  public CrearUserCaseResilience(UserRepository repo, ResilienceService resilience, ReactiveTx tx) {
    this.repo = repo;
    this.resilience = resilience;
    this.tx = tx;
  }

  @Override
  public Mono<User> createUser(User user) {
    return resilience.executeWithResilience(() -> tx.write(() -> repo.save(user)

    ));
  }

  @Override
  public CrearUserStrategy getType() {
    return CrearUserStrategy.RESILIENTE;
  }
}

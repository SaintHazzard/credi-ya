package co.com.bancolombia.usecase.creacionuser.creacion;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.creacionuser.ResilienceService;
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
        boolean fail = Math.random() > 0.1;
        if (fail) {
          return Mono.error(new RuntimeException("Simulated random failure 🚨"));
        }
        return repo.save(user); // éxito
      }));
    }


    @Override
    public CrearUserStrategy getType() {
      return CrearUserStrategy.RESILIENTE;
    }
}

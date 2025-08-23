package co.com.bancolombia.config.user;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.creacionuser.ActualizarUserUseCase;
import co.com.bancolombia.usecase.creacionuser.EliminarUserUseCase;
import co.com.bancolombia.usecase.creacionuser.ManagementUserUseCase;
import co.com.bancolombia.usecase.creacionuser.ResilienceService;
import co.com.bancolombia.usecase.creacionuser.creacion.CrearUserCaseResilience;
import co.com.bancolombia.usecase.creacionuser.creacion.CrearUserStrategy;
import co.com.bancolombia.usecase.creacionuser.creacion.CrearUserUseCase;
import co.com.bancolombia.usecase.creacionuser.creacion.CrearUsuarioStrategy;
import co.com.bancolombia.usecase.creacionuser.creacion.DeletegateCrearUser;

@Configuration
public class UserManagementUseCasesConfig {

  @Bean
  public DeletegateCrearUser delegateCrearUserUseCase(List<CrearUsuarioStrategy> strategies) {
    Map<CrearUserStrategy, CrearUsuarioStrategy> map = strategies.stream()
        .collect(Collectors.toMap(CrearUsuarioStrategy::getType, s -> s));

    return new DeletegateCrearUser(map);
  }

  @Bean
  public CrearUsuarioStrategy crearUserSimpleStrategy(UserRepository repo) {
    return new CrearUserUseCase(repo);
  }

  @Bean
  public CrearUsuarioStrategy crearUserResilienteStrategy(UserRepository repo, ResilienceService resilience) {
    return new CrearUserCaseResilience(repo, resilience);
  }

  @Bean
  public ActualizarUserUseCase actualizarUserUseCase(UserRepository repo) {
    return new ActualizarUserUseCase(repo);
  }

  @Bean
  public EliminarUserUseCase eliminarUserUseCase(UserRepository repo) {
    return new EliminarUserUseCase(repo);
  }

  @Bean
  public ManagementUserUseCase managementUserUseCase(UserRepository repo) {
    return new ManagementUserUseCase(repo);
  }
}

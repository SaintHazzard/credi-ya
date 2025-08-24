package co.com.bancolombia.config.user;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.bancolombia.model.user.common.ReactiveTx;
import co.com.bancolombia.model.user.common.ResilienceService;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.user.validator.UserValidatorPort;
import co.com.bancolombia.usecase.creacionuser.ActualizarUserUseCase;
import co.com.bancolombia.usecase.creacionuser.EliminarUserUseCase;
import co.com.bancolombia.usecase.creacionuser.ManagementUserUseCase;
import co.com.bancolombia.usecase.creacionuser.creacion.CrearUserCaseResilience;
import co.com.bancolombia.usecase.creacionuser.creacion.CrearUserStrategy;
import co.com.bancolombia.usecase.creacionuser.creacion.CrearUserUseCase;
import co.com.bancolombia.usecase.creacionuser.creacion.CrearUsuarioStrategy;
import co.com.bancolombia.usecase.creacionuser.creacion.DeletegateCrearUser;
import co.com.bancolombia.usecase.creacionuser.creacion.ValidatingCrearUsuarioDecorator;
import co.com.bancolombia.usecase.validator.UserValidatorUseCase;

@Configuration
public class UserManagementUseCasesConfig {

  @Bean
  public UserValidatorPort userValidator(UserRepository userRepository) {
    return new UserValidatorUseCase(userRepository);
  }

  @Bean
  public DeletegateCrearUser delegateCrearUserUseCase(List<CrearUsuarioStrategy> strategies) {
    Map<CrearUserStrategy, CrearUsuarioStrategy> map = strategies.stream()
        .collect(Collectors.toMap(CrearUsuarioStrategy::getType, s -> s));

    return new DeletegateCrearUser(map);
  }

  /**
   * Estrategia simple para crear usuarios, decorada con validación
   */
  @Bean
  public CrearUsuarioStrategy crearUserSimpleStrategy(UserRepository repo, UserValidatorPort validator) {
    CrearUserUseCase simpleStrategy = new CrearUserUseCase(repo);
    return new ValidatingCrearUsuarioDecorator(simpleStrategy, validator);
  }

  /**
   * Estrategia resiliente para crear usuarios, decorada con validación
   */
  @Bean
  public CrearUsuarioStrategy crearUserResilienteStrategy(
      UserRepository repo,
      ResilienceService resilience,
      ReactiveTx tx,
      UserValidatorPort validator) {
    
    CrearUserCaseResilience resilientStrategy = new CrearUserCaseResilience(repo, resilience, tx);
    return new ValidatingCrearUsuarioDecorator(resilientStrategy, validator);
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

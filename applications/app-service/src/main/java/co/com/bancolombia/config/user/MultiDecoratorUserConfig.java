package co.com.bancolombia.config.user;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.bancolombia.model.user.common.ReactiveTx;
import co.com.bancolombia.model.user.common.ResilienceService;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.user.logger.LoggingPort;
// import co.com.bancolombia.model.user.security.SecurityPort;
import co.com.bancolombia.model.user.validator.UserValidatorPort;
import co.com.bancolombia.usecase.creacionuser.ActualizarUserUseCase;
import co.com.bancolombia.usecase.creacionuser.EliminarUserUseCase;
import co.com.bancolombia.usecase.creacionuser.ManagementUserUseCase;
import co.com.bancolombia.usecase.creacionuser.creacion.CrearUserCaseResilience;
import co.com.bancolombia.usecase.creacionuser.creacion.CrearUserUseCase;
import co.com.bancolombia.usecase.creacionuser.creacion.CrearUsuarioStrategy;
import co.com.bancolombia.usecase.creacionuser.creacion.DelegateCrearUserService;
import co.com.bancolombia.usecase.creacionuser.creacion.LoggingCrearUsuarioDecorator;
import co.com.bancolombia.usecase.creacionuser.creacion.MultiDecoratorCrearUsuarioFactory;
// import co.com.bancolombia.usecase.creacionuser.creacion.SecurityCrearUsuarioDecorator;
import co.com.bancolombia.usecase.creacionuser.creacion.ValidatingCrearUsuarioDecorator;
import co.com.bancolombia.usecase.validator.UserValidatorUseCase;

/**
 * Ejemplo de configuración avanzada con múltiples decoradores
 * Esta clase es solo un ejemplo y no debe reemplazar tu configuración actual
 */
@Configuration
public class MultiDecoratorUserConfig {

  @Bean
  public UserValidatorPort userValidator(UserRepository userRepository) {
    return new UserValidatorUseCase(userRepository);
  }

  /**
   * Estrategia simple para crear usuarios
   */
  @Bean
  public CrearUsuarioStrategy crearUserSimpleStrategy(UserRepository repo) {
    return new CrearUserUseCase(repo);
  }

  /**
   * Estrategia resiliente para crear usuarios
   */
  @Bean
  public CrearUsuarioStrategy crearUserResilienteStrategy(
      UserRepository repo,
      ResilienceService resilience,
      ReactiveTx tx) {
    
    return new CrearUserCaseResilience(repo, resilience, tx);
  }
  
  /**
   * Fábrica que aplica múltiples decoradores a las estrategias
   */
  @Bean
  public MultiDecoratorCrearUsuarioFactory multiDecoratorFactory(
      List<CrearUsuarioStrategy> strategies,
      UserValidatorPort validator,
      LoggingPort logger
      // SecurityPort security
      ) {
    
    // Creamos la lista de decoradores en el orden que queremos aplicarlos
    List<Function<CrearUsuarioStrategy, CrearUsuarioStrategy>> decorators = new ArrayList<>();
    
    // 1. Primero aplicamos validación
    decorators.add(strategy -> new ValidatingCrearUsuarioDecorator(strategy, validator));
    
    // 2. Luego aplicamos seguridad
    // decorators.add(strategy -> new SecurityCrearUsuarioDecorator(strategy, security));
    
    // 3. Finalmente aplicamos logging (para registrar después de todas las validaciones)
    decorators.add(strategy -> new LoggingCrearUsuarioDecorator(strategy, logger));
    
    // Creamos la fábrica con las estrategias y los decoradores
    MultiDecoratorCrearUsuarioFactory factory = new MultiDecoratorCrearUsuarioFactory(strategies, decorators);
    factory.init(); // Inicializa y decora todas las estrategias
    return factory;
  }
  
  /**
   * Servicio delegado que usa la fábrica para acceder a las estrategias decoradas
   */
  @Bean
  public DelegateCrearUserService delegateCrearUserService(MultiDecoratorCrearUsuarioFactory factory) {
    return new DelegateCrearUserService(factory);
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

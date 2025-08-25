package co.com.bancolombia.config.user;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.bancolombia.model.common.CrearStrategy;
import co.com.bancolombia.model.common.ReactiveTx;
import co.com.bancolombia.model.common.ResilienceService;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.user.logger.LoggingPort;
// import co.com.bancolombia.model.user.security.SecurityPort;
import co.com.bancolombia.model.user.validator.UserValidatorPort;
import co.com.bancolombia.r2dbc.implementaciones.users.DelegateCrearUserService;
import co.com.bancolombia.r2dbc.implementaciones.users.LoggingCrearUsuarioDecorator;
import co.com.bancolombia.r2dbc.implementaciones.users.ReactiveTxUserDecorator;
import co.com.bancolombia.r2dbc.implementaciones.users.UserCreationStrategyFactory;
import co.com.bancolombia.r2dbc.implementaciones.users.ValidatingCrearUsuarioDecorator;
import co.com.bancolombia.r2dbc.implementaciones.users.validator.UserValidatorUseCase;
import co.com.bancolombia.usecase.userCases.ActualizarUserUseCase;
import co.com.bancolombia.usecase.userCases.EliminarUserUseCase;
import co.com.bancolombia.usecase.userCases.creacion.CrearUserCaseResilience;
import co.com.bancolombia.usecase.userCases.creacion.CrearUserUseCase;

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
  public CrearStrategy<User> crearUserSimpleStrategy(UserRepository repo, UserValidatorPort validator) {
    return new CrearUserUseCase(repo, validator);
  }

  /**
   * Estrategia resiliente para crear usuarios
   */
  @Bean
  public CrearStrategy<User> crearUserResilienteStrategy(
      UserRepository repo,
      ResilienceService resilience) {

    return new CrearUserCaseResilience(repo, resilience);
  }
  
  /**
   * Fábrica que aplica múltiples decoradores a las estrategias
   */
  @Bean
  public UserCreationStrategyFactory userCreationStrategyFactory(
      List<CrearStrategy<User>> strategies,
      UserValidatorPort validator,
     LoggingPort logger,
      ReactiveTx reactiveTx
      ) {
    
    // Creamos la lista de decoradores en el orden que queremos aplicarlos
    List<Function<CrearStrategy<User>, CrearStrategy<User>>> decorators = new ArrayList<>();
    
    // 1. Primero aplicamos validación
    decorators.add(strategy -> new ValidatingCrearUsuarioDecorator(strategy, validator));
    
    // 2. Luego aplicamos seguridad (comentado por ahora)
    // decorators.add(strategy -> new SecurityCrearUsuarioDecorator(strategy, security));
    
    // 3. Aplicamos transacciones reactivas
    decorators.add(strategy -> new ReactiveTxUserDecorator(strategy, reactiveTx));
    
    // 4. Finalmente aplicamos logging (para registrar después de todas las validaciones y transacciones)
    decorators.add(strategy -> new LoggingCrearUsuarioDecorator(strategy, logger));

    // Creamos la fábrica con las estrategias y los decoradores
    return new UserCreationStrategyFactory(strategies, decorators);
  }
  
  /**
   * Servicio delegado que usa la fábrica para acceder a las estrategias decoradas
   */
  @Bean
  public DelegateCrearUserService delegateCrearUserService(
      UserCreationStrategyFactory factory,
      ReactiveTx reactiveTx) {
    return new DelegateCrearUserService(factory, reactiveTx);
  }
  
  @Bean
  public ActualizarUserUseCase actualizarUserUseCase(UserRepository repo) {
    return new ActualizarUserUseCase(repo);
  }

  @Bean
  public EliminarUserUseCase eliminarUserUseCase(UserRepository repo) {
    return new EliminarUserUseCase(repo);
  }


}

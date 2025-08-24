// package co.com.bancolombia.config.user;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.function.Function;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import co.com.bancolombia.model.common.ResilienceService;
// import co.com.bancolombia.model.user.gateways.CrearUsuarioStrategy;
// import co.com.bancolombia.model.user.gateways.StrategyFactory;
// import co.com.bancolombia.model.user.gateways.UserRepository;
// import co.com.bancolombia.model.user.validator.UserValidatorPort;
// import co.com.bancolombia.r2dbc.implementaciones.users.DelegateCrearUserService;
// import co.com.bancolombia.r2dbc.implementaciones.users.UserCreationStrategyFactory;
// import co.com.bancolombia.r2dbc.implementaciones.users.ValidatingCrearUsuarioDecorator;
// import co.com.bancolombia.usecase.creacionuser.ActualizarUserUseCase;
// import co.com.bancolombia.usecase.creacionuser.EliminarUserUseCase;
// import co.com.bancolombia.usecase.creacionuser.ManagementUserUseCase;
// import co.com.bancolombia.usecase.creacionuser.creacion.CrearUserCaseResilience;
// import co.com.bancolombia.usecase.creacionuser.creacion.CrearUserUseCase;
// import co.com.bancolombia.usecase.validator.UserValidatorUseCase;

// /**
//  * Ejemplo de configuración usando la fábrica genérica con decoradores
//  */
// @Configuration
// public class GenericDecoratorConfig {

//   @Bean
//   public UserValidatorPort userValidator(UserRepository userRepository) {
//     return new UserValidatorUseCase(userRepository);
//   }

//   /**
//    * Estrategia simple para crear usuarios
//    */
//   @Bean
//   public CrearUsuarioStrategy crearUserSimpleStrategy(UserRepository repo) {
//     return new CrearUserUseCase(repo);
//   }

//   /**
//    * Estrategia resiliente para crear usuarios
//    */
//   @Bean
//   public CrearUsuarioStrategy crearUserResilienteStrategy(
//       UserRepository repo,
//       ResilienceService resilience) {
    
//     return new CrearUserCaseResilience(repo, resilience);
//   }
  
//   /**
//    * Fábrica que usa genéricos para aplicar decoradores
//    */
//   @Bean
//   public StrategyFactory userCreationStrategyFactory(
//       List<CrearUsuarioStrategy> strategies,
//       UserValidatorPort validator) {
    
//     // Creamos la lista de decoradores
//     List<Function<CrearUsuarioStrategy, CrearUsuarioStrategy>> decorators = new ArrayList<>();
    
//     // Añadimos el decorador de validación
//     decorators.add(strategy -> new ValidatingCrearUsuarioDecorator(strategy, validator));
    
//     // Podríamos añadir más decoradores aquí si los tuviéramos
    
//     // Creamos la fábrica específica para CrearUsuarioStrategy
//     return new UserCreationStrategyFactory(strategies, decorators);
//   }
  
//   /**
//    * Servicio delegado que usa la fábrica
//    */
//   @Bean
//   public DelegateCrearUserService delegateCrearUserService(StrategyFactory factory) {
//     return new DelegateCrearUserService(factory);
//   }
  
//   @Bean
//   public ActualizarUserUseCase actualizarUserUseCase(UserRepository repo) {
//     return new ActualizarUserUseCase(repo);
//   }

//   @Bean
//   public EliminarUserUseCase eliminarUserUseCase(UserRepository repo) {
//     return new EliminarUserUseCase(repo);
//   }

//   @Bean
//   public ManagementUserUseCase managementUserUseCase(UserRepository repo) {
//     return new ManagementUserUseCase(repo);
//   }
// }

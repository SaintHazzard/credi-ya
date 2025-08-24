// package co.com.bancolombia.config.user;

// import java.util.List;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import co.com.bancolombia.model.common.ResilienceService;
// import co.com.bancolombia.model.user.gateways.CrearUsuarioStrategy;
// import co.com.bancolombia.model.user.gateways.UserRepository;
// import co.com.bancolombia.model.user.validator.UserValidatorPort;
// import co.com.bancolombia.r2dbc.implementaciones.users.DelegateCrearUserService;
// import co.com.bancolombia.r2dbc.implementaciones.users.ValidatedCrearUsuarioFactory;
// import co.com.bancolombia.usecase.creacionuser.ActualizarUserUseCase;
// import co.com.bancolombia.usecase.creacionuser.EliminarUserUseCase;
// import co.com.bancolombia.usecase.creacionuser.ManagementUserUseCase;
// import co.com.bancolombia.usecase.creacionuser.creacion.CrearUserCaseResilience;
// import co.com.bancolombia.usecase.creacionuser.creacion.CrearUserUseCase;
// import co.com.bancolombia.usecase.validator.UserValidatorUseCase;

// @Configuration
// public class UserManagementUseCasesConfig {

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
//    * Fábrica para decorar automáticamente todas las estrategias con validación
//    */
//   @Bean
//   public ValidatedCrearUsuarioFactory validatedCrearUsuarioFactory(
//       UserValidatorPort validator, 
//       List<CrearUsuarioStrategy> strategies) {
//     ValidatedCrearUsuarioFactory factory = new ValidatedCrearUsuarioFactory(validator, strategies);
//     factory.init(); // Inicializa y decora todas las estrategias
//     return factory;
//   }
  
//   /**
//    * Servicio delegado que usa la fábrica para acceder a las estrategias validadas
//    */
//   @Bean
//   public DelegateCrearUserService delegateCrearUserService(ValidatedCrearUsuarioFactory factory) {
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

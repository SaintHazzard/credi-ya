package co.com.bancolombia.usecase.validator;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.user.validator.UserValidatorPort;
import co.com.bancolombia.model.user.validator.ValidationError;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Implementación del servicio de validación de usuarios
 */
@RequiredArgsConstructor
public class UserValidatorUseCase implements UserValidatorPort {

    private final UserRepository userRepository;
    
    // Constantes para validación
    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Override
    public Mono<User> validateUser(User user) {
        // Creamos un builder para acumular errores
        ValidationError.ValidationErrors.Builder errorsBuilder = new ValidationError.ValidationErrors.Builder();
        
        // Validar campos requeridos
        if (user.getNames() == null || user.getNames().trim().isEmpty()) {
            errorsBuilder.addError("names", "El nombre no puede ser nulo o vacío");
        }
        
        if (user.getLastname() == null || user.getLastname().trim().isEmpty()) {
            errorsBuilder.addError("lastname", "Los apellidos no pueden ser nulos o vacíos");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errorsBuilder.addError("email", "El correo electrónico no puede ser nulo o vacío");
        } else if (!user.getEmail().matches(EMAIL_REGEX)) {
            errorsBuilder.addError("email", "El formato del correo electrónico es inválido");
        }
        
        // Validar salario
        if (user.getSalaryBase() == null) {
            errorsBuilder.addError("salaryBase", "El salario base no puede ser nulo");
        } else if (user.getSalaryBase().compareTo(MIN_SALARY) < 0 || user.getSalaryBase().compareTo(MAX_SALARY) > 0) {
            errorsBuilder.addError("salaryBase", "El salario base debe estar entre 0 y 15,000,000");
        }
        
        // Si hay errores, devolver un Mono.error
        if (errorsBuilder.hasErrors()) {
            return Mono.error(errorsBuilder.build());
        }
        
        // Si no hay errores, devolver el usuario
        return Mono.just(user);
    }

    @Override
    public Mono<Boolean> validateUniqueEmail(String email) {
        // Buscar si existe un usuario con el mismo email
        return userRepository.findByEmail(email)
            .hasElement()
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new ValidationError.ValidationErrors.Builder()
                        .addError("email", "El correo electrónico ya está registrado")
                        .build());
                }
                return Mono.just(true);
            });
    }
}

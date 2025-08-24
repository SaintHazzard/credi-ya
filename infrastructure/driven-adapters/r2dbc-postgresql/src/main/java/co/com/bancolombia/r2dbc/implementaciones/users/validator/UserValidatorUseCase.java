package co.com.bancolombia.r2dbc.implementaciones.users.validator;

import co.com.bancolombia.model.common.ValidationError;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.user.validator.UserValidatorPort;
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

        validateUniqueEmail(user.getEmail())
                .doOnError(error -> errorsBuilder.addError(user.getEmail().getClass().getName(), error.getMessage()))
                .subscribe();
        // validar salario
        validateSalary(user.getSalaryBase())
                .doOnError(error -> errorsBuilder.addError(user.getSalaryBase().getClass().getName(), error.getMessage()))
                .subscribe();

        // validar email
        if (user.getEmail() == null || !user.getEmail().matches(EMAIL_REGEX)) {
            errorsBuilder.addError("email", "El correo electrónico no tiene un formato válido");
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

    @Override
    public Mono<Boolean> validateSalary(BigDecimal salary) {
        if (salary == null) {
            return Mono.error(new ValidationError.ValidationErrors.Builder()
                    .addError("salary", "El salario no puede ser nulo")
                    .build());
        }
        if (salary.compareTo(MIN_SALARY) < 0 || salary.compareTo(MAX_SALARY) > 0) {
            return Mono.error(new ValidationError.ValidationErrors.Builder()
                    .addError("salary", "El salario debe estar entre 0 y 15,000,000")
                    .build());
        }
        return Mono.just(true);
    }
}

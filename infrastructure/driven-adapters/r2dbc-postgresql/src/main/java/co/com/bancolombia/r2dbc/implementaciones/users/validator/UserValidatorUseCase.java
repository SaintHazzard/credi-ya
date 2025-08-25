package co.com.bancolombia.r2dbc.implementaciones.users.validator;

import co.com.bancolombia.model.common.ValidationError;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.user.validator.UserValidatorPort;
import co.com.bancolombia.r2dbc.dtos.user.UserDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Implementación del servicio de validación de usuarios
 */
@RequiredArgsConstructor
public class UserValidatorUseCase implements UserValidatorPort {

    private final UserRepository userRepository;

    // Constantes para validación
    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");

    @Override
    public Mono<User> validateUser(User user) {
        // Creamos un builder para acumular errores
        ValidationError.ValidationErrors.Builder errorsBuilder = new ValidationError.ValidationErrors.Builder();

        // Validar restricciones de Jakarta Bean Validation usando las anotaciones de UserDTO
        Mono<Void> beanValidation = validateUserWithAnnotations(user, errorsBuilder);

        // Validar email único (regla de negocio)
        Mono<Void> uniqueEmailValidation = validateUniqueEmail(user.getEmail())
                .doOnError(error -> errorsBuilder.addError("email", error.getMessage()))
                .then();

        // Validar salario (regla de negocio adicional si se necesita)
        Mono<Void> salaryValidation = validateSalary(user.getSalaryBase())
                .doOnError(error -> errorsBuilder.addError("salaryBase", error.getMessage()))
                .then();

        // Combinar todas las validaciones
        return Mono.when(beanValidation, uniqueEmailValidation, salaryValidation)
                .then(Mono.defer(() -> {
                    ValidationError.ValidationErrors errors = errorsBuilder.build();
                    if (errors != null && !errors.getErrors().isEmpty()) {
                        return Mono.error(errors);
                    }
                    return Mono.just(user);
                }));
    }

    /**
     * Valida el usuario utilizando las anotaciones de validación definidas en UserDTO
     */
    private Mono<Void> validateUserWithAnnotations(User user, ValidationError.ValidationErrors.Builder errorsBuilder) {
        return Mono.fromCallable(() -> {
            // Convertir el modelo de dominio User a UserDTO para aprovechar las anotaciones
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);

            // Crear un validador de Jakarta
            try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                Validator validator = factory.getValidator();
                
                // Validar el DTO según sus anotaciones
                Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
                
                // Agregar cada violación al builder de errores
                violations.forEach(violation -> 
                    errorsBuilder.addError(
                        violation.getPropertyPath().toString(), 
                        violation.getMessage()
                    )
                );
            }
            return null;
        }).then();
    }

    @Override
    public Mono<Boolean> validateUniqueEmail(String email) {
        if (email == null || email.isEmpty()) {
            return Mono.error(new IllegalArgumentException("El correo electrónico no puede estar vacío"));
        }
        
        // Buscar si existe un usuario con el mismo email
        return userRepository.findByEmail(email)
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("El correo electrónico ya está registrado"));
                    }
                    return Mono.just(true);
                });
    }

    @Override
    public Mono<Boolean> validateSalary(BigDecimal salary) {
        if (salary == null) {
            return Mono.error(new IllegalArgumentException("El salario no puede ser nulo"));
        }
        if (salary.compareTo(MIN_SALARY) < 0 || salary.compareTo(MAX_SALARY) > 0) {
            return Mono.error(new IllegalArgumentException("El salario debe estar entre 0 y 15,000,000"));
        }
        return Mono.just(true);
    }
}

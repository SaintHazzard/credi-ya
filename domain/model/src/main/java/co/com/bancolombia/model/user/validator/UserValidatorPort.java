package co.com.bancolombia.model.user.validator;

import java.math.BigDecimal;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

/**
 * Puerto para validación de usuarios
 * Define las operaciones de validación que se pueden aplicar a un usuario
 */
public interface UserValidatorPort {
    
    /**
     * Valida un usuario antes de su creación o actualización
     * @param user Usuario a validar
     * @return Mono con el usuario si es válido, o error si no lo es
     */
    Mono<User> validateUser(User user);
    
    /**
     * Valida que el correo no esté duplicado
     * @param email Correo a validar
     * @return Mono con true si el correo no está duplicado, o error si lo está
     */
    Mono<Boolean> validateUniqueEmail(String email);


    Mono<Boolean> validateSalary(BigDecimal salary);
}

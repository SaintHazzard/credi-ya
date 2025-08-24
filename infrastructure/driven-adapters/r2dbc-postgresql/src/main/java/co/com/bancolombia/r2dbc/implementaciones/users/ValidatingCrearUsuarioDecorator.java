package co.com.bancolombia.r2dbc.implementaciones.users;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.CrearUserStrategyEnum;
import co.com.bancolombia.model.user.gateways.CrearUsuarioStrategy;
import co.com.bancolombia.model.user.validator.UserValidatorPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Decorador que aplica validaciones a cualquier implementación de CrearUsuarioStrategy
 * Utilizando el patrón Decorator para añadir comportamiento sin modificar las clases existentes
 */
@RequiredArgsConstructor
public class ValidatingCrearUsuarioDecorator implements CrearUsuarioStrategy {

    private final CrearUsuarioStrategy delegate;
    private final UserValidatorPort validator;

    @Override
    public CrearUserStrategyEnum getType() {
        return delegate.getType();
    }

    @Override
    public Mono<User> createUser(User user) {
        // Primero validamos el usuario
        return validator.validateUser(user)
            // Luego validamos que el correo sea único
            .flatMap(validUser -> validator.validateUniqueEmail(validUser.getEmail())
                // Si todo es válido, delegamos a la implementación original
                .then(delegate.createUser(validUser))
            );
    }
}

package co.com.bancolombia.r2dbc.implementaciones.users;

import co.com.bancolombia.model.common.CrearStrategy;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.CrearStrategyEnum;
import co.com.bancolombia.model.user.validator.UserValidatorPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Decorador que aplica validaciones a cualquier implementación de
 * CrearUsuarioStrategy
 * Utilizando el patrón Decorator para añadir comportamiento sin modificar las
 * clases existentes
 */
@RequiredArgsConstructor
public class ValidatingCrearUsuarioDecorator implements CrearStrategy<User> {

    private final CrearStrategy<User> delegate;
    private final UserValidatorPort validator;

    @Override
    public CrearStrategyEnum getType() {
        return delegate.getType();
    }

    // @Override
    // public Mono<User> create(User user) {
    // // Primero validamos el usuario
    // return validator.validateUser(user)
    // // Luego validamos que el correo sea único
    // .flatMap(validUser -> validator.validateUniqueEmail(validUser.getEmail())
    // // Si todo es válido, delegamos a la implementación original
    // .then(delegate.create(validUser))
    // );
    // }

    @Override
    public Mono<User> create(User user) {
        return validator.validateUniqueEmail(user.getEmail())
                .flatMap(validEmail -> {
                    if (!validEmail) {
                        return Mono.error(new IllegalArgumentException("El correo electrónico ya está en uso"));
                    }
                    return delegate.create(user);
                });
    }
}

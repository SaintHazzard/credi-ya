package co.com.bancolombia.r2dbc.implementaciones.users;

import co.com.bancolombia.model.common.CrearStrategy;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.CrearStrategyEnum;
import co.com.bancolombia.model.user.logger.LoggingPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Decorador que añade capacidades de logging a cualquier implementación de CrearUsuarioStrategy
 */
@RequiredArgsConstructor
public class LoggingCrearUsuarioDecorator implements CrearStrategy<User> {

    private final CrearStrategy<User> delegate;
    private final LoggingPort logger;

    @Override
    public CrearStrategyEnum getType() {
        return delegate.getType();
    }

    @Override
    public Mono<User> create(User user) {
        logger.info("Iniciando creación de usuario: " + user.getEmail());
        
        return delegate.create(user)
            .doOnSuccess(createdUser -> 
                logger.info("Usuario creado exitosamente: " + createdUser.getId())
            )
            .doOnError(error -> 
                logger.error("Error al crear usuario: " + user.getEmail(), error)
            );
    }
}

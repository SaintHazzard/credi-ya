package co.com.bancolombia.usecase.creacionuser.creacion;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.logger.LoggingPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Decorador que añade capacidades de logging a cualquier implementación de CrearUsuarioStrategy
 */
@RequiredArgsConstructor
public class LoggingCrearUsuarioDecorator implements CrearUsuarioStrategy {

    private final CrearUsuarioStrategy delegate;
    private final LoggingPort logger;

    @Override
    public CrearUserStrategyEnum getType() {
        return delegate.getType();
    }

    @Override
    public Mono<User> createUser(User user) {
        logger.info("Iniciando creación de usuario: " + user.getEmail());
        
        return delegate.createUser(user)
            .doOnSuccess(createdUser -> 
                logger.info("Usuario creado exitosamente: " + createdUser.getId())
            )
            .doOnError(error -> 
                logger.error("Error al crear usuario: " + user.getEmail(), error)
            );
    }
}

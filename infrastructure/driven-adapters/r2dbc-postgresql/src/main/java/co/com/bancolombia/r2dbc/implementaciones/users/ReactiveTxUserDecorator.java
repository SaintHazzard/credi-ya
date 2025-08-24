package co.com.bancolombia.r2dbc.implementaciones.users;

import co.com.bancolombia.model.common.ReactiveTx;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.CrearUserStrategyEnum;
import co.com.bancolombia.model.user.gateways.CrearUsuarioStrategy;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Decorador que añade transacciones reactivas a cualquier estrategia de creación de usuarios
 * Asegura que todas las operaciones de base de datos se ejecuten dentro de una transacción
 */
@RequiredArgsConstructor
public class ReactiveTxUserDecorator implements CrearUsuarioStrategy {

    private final CrearUsuarioStrategy delegate;
    private final ReactiveTx reactiveTx;

    @Override
    public CrearUserStrategyEnum getType() {
        return delegate.getType();
    }

    @Override
    public Mono<User> createUser(User user) {
        // Ejecutamos la operación dentro de una transacción de escritura
        return reactiveTx.write(() -> delegate.createUser(user));
    }
}

package co.com.bancolombia.usecase.creacionuser.creacion;

import co.com.bancolombia.model.user.User;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Servicio que delega la creación de usuarios a la estrategia apropiada
 * Utiliza la fábrica de estrategias validadas
 */
@RequiredArgsConstructor
public class DelegateCrearUserService {

    private final ValidatedCrearUsuarioFactory factory;
    
    /**
     * Crea un usuario utilizando la estrategia simple
     */
    public Mono<User> createUser(User user) {
        return factory.getStrategy(CrearUserStrategyEnum.SIMPLE).createUser(user);
    }
    
    /**
     * Crea un usuario utilizando la estrategia resiliente
     */
    public Mono<User> createUserWithResilience(User user) {
        return factory.getStrategy(CrearUserStrategyEnum.RESILIENTE).createUser(user);
    }
    
    /**
     * Crea un usuario utilizando una estrategia específica
     */
    public Mono<User> createUserWithStrategy(User user, CrearUserStrategyEnum strategy) {
        return factory.getStrategy(strategy).createUser(user);
    }
}

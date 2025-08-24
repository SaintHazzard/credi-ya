package co.com.bancolombia.usecase.creacionuser.creacion;

import co.com.bancolombia.model.user.User;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Servicio que delega la creación de usuarios a la estrategia apropiada
 * Utiliza la fábrica de estrategias
 */
@RequiredArgsConstructor
public class DelegateCrearUserService {

    private final StrategyFactory factory;
    
    /**
     * Crea un usuario seleccionando automáticamente la estrategia adecuada
     * Si el nombre es largo (>25 caracteres), usa la estrategia resiliente
     * De lo contrario, usa la estrategia simple
     */
    public Mono<User> createUser(User user) {
        if (user.getNames().length() > 25) {
            return factory.getStrategy(CrearUserStrategyEnum.RESILIENTE).createUser(user);
        }
        return factory.getStrategy(CrearUserStrategyEnum.SIMPLE).createUser(user);
    }
    
    /**
     * Crea un usuario utilizando la estrategia simple
     */
    public Mono<User> createUserSimple(User user) {
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

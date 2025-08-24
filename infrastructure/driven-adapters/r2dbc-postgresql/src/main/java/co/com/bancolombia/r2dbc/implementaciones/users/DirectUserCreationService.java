package co.com.bancolombia.r2dbc.implementaciones.users;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import co.com.bancolombia.model.common.CrearStrategy;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.CrearStrategyEnum;
import reactor.core.publisher.Mono;

/**
 * Servicio directo para crear usuarios utilizando diferentes estrategias
 * Esta clase es una alternativa a DelegateCrearUserService
 */
@Service
public class DirectUserCreationService {

    private final CrearStrategy<User> simpleStrategy;
    private final CrearStrategy<User> resilientStrategy;
    
    public DirectUserCreationService(
            @Qualifier("crearUserSimpleStrategy") CrearStrategy<User> simpleStrategy,
            @Qualifier("crearUserResilienteStrategy") CrearStrategy<User> resilientStrategy) {
        this.simpleStrategy = simpleStrategy;
        this.resilientStrategy = resilientStrategy;
    }
    
    /**
     * Crea un usuario seleccionando automáticamente la estrategia adecuada
     * Si el nombre es largo (>25 caracteres), usa la estrategia resiliente
     * De lo contrario, usa la estrategia simple
     */
    public Mono<User> create(User user) {
        if (user.getNames().length() > 25) {
            return createUserWithResilience(user);
        }
        return createUserSimple(user);
    }
    
    /**
     * Crea un usuario utilizando la estrategia simple
     */
    public Mono<User> createUserSimple(User user) {
        return simpleStrategy.create(user);
    }
    
    /**
     * Crea un usuario utilizando la estrategia resiliente
     */
    public Mono<User> createUserWithResilience(User user) {
        return resilientStrategy.create(user);
    }
    
    /**
     * Crea un usuario utilizando una estrategia específica
     */
    public Mono<User> createUserWithStrategy(User user, CrearStrategyEnum strategy) {
        switch (strategy) {
            case SIMPLE:
                return createUserSimple(user);
            case RESILIENTE:
                return createUserWithResilience(user);
            default:
                throw new IllegalArgumentException("Estrategia no soportada: " + strategy);
        }
    }
}

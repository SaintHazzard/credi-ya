package co.com.bancolombia.r2dbc.implementaciones.users;

import java.util.List;

import co.com.bancolombia.model.common.ReactiveTx;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.CrearStrategyEnum;
import co.com.bancolombia.model.user.gateways.StrategyUserFactory;
import co.com.bancolombia.model.user.gateways.UserCreationPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Servicio que delega la creación de usuarios a la estrategia apropiada
 * Utiliza la fábrica de estrategias
 */
@RequiredArgsConstructor
public class DelegateCrearUserService implements UserCreationPort {

    private final StrategyUserFactory factory;
    // private final ManagementUserUseCase managementUserUseCase;
    private final ReactiveTx reactiveTx;

    /**
     * Crea un usuario seleccionando automáticamente la estrategia adecuada
     * Si el nombre es largo (>25 caracteres), usa la estrategia resiliente
     * De lo contrario, usa la estrategia simple
     */
    public Mono<User> create(User user) {
        if (user.getNames().length() > 25) {
            return factory.getStrategy(CrearStrategyEnum.RESILIENTE).create(user);
        }
        return factory.getStrategy(CrearStrategyEnum.SIMPLE).create(user);
    }
    
    /**
     * Crea un usuario utilizando la estrategia simple
     */
    public Mono<User> createUserSimple(User user) {
        return factory.getStrategy(CrearStrategyEnum.SIMPLE).create(user);
    }
    
    /**
     * Crea un usuario utilizando la estrategia resiliente
     */
    public Mono<User> createUserWithResilience(User user) {
        return factory.getStrategy(CrearStrategyEnum.RESILIENTE).create(user);
    }
    
    /**
     * Crea un usuario utilizando una estrategia específica
     */
    public Mono<User> createUserWithStrategy(User user, CrearStrategyEnum strategy) {
        return factory.getStrategy(strategy).create(user);
    }
    
    /**
     * Crea múltiples usuarios en una única transacción
     * Si alguno falla, todos fallan (transacción atómica)
     */
    public Mono<Void> createMultipleUsers(List<User> users) {
        return reactiveTx.write(() -> Flux.fromIterable(users)
                .concatMap(this::create) // o flatMapSequential(u -> create(u), 1, 1)
                .then());
    }
    
    /**
     * Crea dos usuarios en una única transacción
     * Si alguno falla, todos fallan (transacción atómica)
     */
    public Mono<Void> createTwoUsers(User user1, User user2) {
        return reactiveTx.write(() -> {
            return create(user1)
                .then(create(user2))
                .then();
        });
    }
}

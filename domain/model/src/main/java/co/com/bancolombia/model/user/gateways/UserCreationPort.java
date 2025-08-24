package co.com.bancolombia.model.user.gateways;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Puerto para la creación de usuarios
 * Define las operaciones relacionadas con la creación de usuarios
 */
public interface UserCreationPort {
    /**
     * Crea un usuario seleccionando automáticamente la estrategia adecuada
     */
    Mono<User> create(User user);
    
    /**
     * Crea un usuario utilizando la estrategia simple
     */
    Mono<User> createUserSimple(User user);
    
    /**
     * Crea un usuario utilizando la estrategia resiliente
     */
    Mono<User> createUserWithResilience(User user);
    
    /**
     * Crea un usuario utilizando una estrategia específica
     */
    Mono<User> createUserWithStrategy(User user, CrearStrategyEnum strategy);
    
    /**
     * Crea múltiples usuarios en una única transacción
     */
    Mono<Void> createMultipleUsers(List<User> users);
    
    /**
     * Crea dos usuarios en una única transacción
     */
    Mono<Void> createTwoUsers(User user1, User user2);
}

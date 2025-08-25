package co.com.bancolombia.usecase.userCases;

import co.com.bancolombia.model.log.LoggerPort;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Caso de uso de ejemplo que utiliza el puerto de logging directamente
 */
@RequiredArgsConstructor
public class ConsultaUserUseCase {

    private final UserRepository userRepository;
    private final LoggerPort logger;

    /**
     * Método que utiliza el puerto de logging directamente
     */
    public Mono<User> consultarUserConLog(String id) {
        // Utilizamos el logger directamente para loggear información específica
        logger.info(this.getClass(), "Consultando usuario con ID: {}", id);
        
        return userRepository.findById(id)
            .doOnSuccess(user -> 
                logger.info(this.getClass(), "Usuario encontrado: {}", user))
            .doOnError(error -> 
                logger.error(this.getClass(), "Error al consultar usuario: {}", error, id));
    }

    public Mono<Boolean> findByEmail(String email) {
        logger.info(this.getClass(), "Consultando si existe usuario con email: {}", email);
        return userRepository.findByEmail(email)
            .map(user -> {
                logger.info(this.getClass(), "Usuario encontrado con email: {}", email);
                return true;
            })
            .defaultIfEmpty(false)
            .doOnError(error -> 
                logger.error(this.getClass(), "Error al consultar usuario por email: {}", error, email));
    }


}

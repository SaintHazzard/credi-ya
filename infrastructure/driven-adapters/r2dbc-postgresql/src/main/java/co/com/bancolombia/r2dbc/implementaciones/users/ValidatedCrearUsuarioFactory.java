package co.com.bancolombia.r2dbc.implementaciones.users;

import co.com.bancolombia.model.common.CrearStrategy;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.CrearStrategyEnum;
import co.com.bancolombia.model.user.gateways.StrategyUserFactory;
import co.com.bancolombia.model.user.validator.UserValidatorPort;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Fábrica para crear estrategias de creación de usuarios con validación
 * Aplica el patrón Decorator a todas las estrategias registradas
 */
@RequiredArgsConstructor
public class ValidatedCrearUsuarioFactory implements StrategyUserFactory {

    private final UserValidatorPort validator;
    private final List<CrearStrategy<User>> strategies;

    // Mapa de estrategias decoradas por tipo
    private Map<CrearStrategyEnum, CrearStrategy<User>> decoratedStrategies;

    /**
     * Inicializa la fábrica decorando todas las estrategias registradas
     */
    public void init() {
        // Decoramos todas las estrategias con el validador
        decoratedStrategies = strategies.stream()
            .collect(Collectors.toMap(
                CrearStrategy::getType, 
                strategy -> new ValidatingCrearUsuarioDecorator(strategy, validator)
            ));
    }
    
    /**
     * Obtiene una estrategia decorada por su tipo
     * @param type Tipo de estrategia
     * @return Estrategia decorada con validaciones
     */
    @Override
    public CrearStrategy<User> getStrategy(CrearStrategyEnum type) {
        return Optional.ofNullable(decoratedStrategies.get(type))
            .orElseThrow(() -> new IllegalArgumentException("Estrategia no encontrada: " + type));
    }
}

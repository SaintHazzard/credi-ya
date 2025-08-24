package co.com.bancolombia.usecase.creacionuser.creacion;

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
public class ValidatedCrearUsuarioFactory implements StrategyFactory {

    private final UserValidatorPort validator;
    private final List<CrearUsuarioStrategy> strategies;
    
    // Mapa de estrategias decoradas por tipo
    private Map<CrearUserStrategyEnum, CrearUsuarioStrategy> decoratedStrategies;
    
    /**
     * Inicializa la fábrica decorando todas las estrategias registradas
     */
    public void init() {
        // Decoramos todas las estrategias con el validador
        decoratedStrategies = strategies.stream()
            .collect(Collectors.toMap(
                CrearUsuarioStrategy::getType, 
                strategy -> new ValidatingCrearUsuarioDecorator(strategy, validator)
            ));
    }
    
    /**
     * Obtiene una estrategia decorada por su tipo
     * @param type Tipo de estrategia
     * @return Estrategia decorada con validaciones
     */
    @Override
    public CrearUsuarioStrategy getStrategy(CrearUserStrategyEnum type) {
        return Optional.ofNullable(decoratedStrategies.get(type))
            .orElseThrow(() -> new IllegalArgumentException("Estrategia no encontrada: " + type));
    }
}

package co.com.bancolombia.usecase.creacionuser.creacion;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

/**
 * Fábrica genérica abstracta que aplica múltiples decoradores a cualquier tipo de estrategia
 * Permite añadir cualquier número de decoradores en un orden específico
 * 
 * @param <S> Tipo de estrategia
 * @param <E> Tipo de enumeración o identificador para clasificar estrategias
 */
@RequiredArgsConstructor
public class MultiDecoratorAbstractFactory<S, E> {

    private final List<S> strategies;
    private final List<Function<S, S>> decorators;
    private final Function<S, E> typeExtractor;
    
    // Mapa de estrategias decoradas por tipo
    private Map<E, S> decoratedStrategies;
    
    /**
     * Inicializa la fábrica aplicando todos los decoradores a todas las estrategias
     */
    public void init() {
        // Decoramos todas las estrategias con todos los decoradores en orden
        decoratedStrategies = strategies.stream()
            .collect(Collectors.toMap(
                typeExtractor, 
                strategy -> applyDecorators(strategy)
            ));
    }
    
    /**
     * Aplica todos los decoradores a una estrategia, en el orden en que fueron registrados
     */
    private S applyDecorators(S strategy) {
        S decoratedStrategy = strategy;
        for (Function<S, S> decorator : decorators) {
            decoratedStrategy = decorator.apply(decoratedStrategy);
        }
        return decoratedStrategy;
    }
    
    /**
     * Obtiene una estrategia decorada por su tipo
     * @param type Tipo de estrategia
     * @return Estrategia decorada con todas las capas configuradas
     */
    public S getStrategy(E type) {
        return Optional.ofNullable(decoratedStrategies.get(type))
            .orElseThrow(() -> new IllegalArgumentException("Estrategia no encontrada: " + type));
    }
}

package co.com.bancolombia.model.user.gateways;

import co.com.bancolombia.model.common.CrearStrategy;
import co.com.bancolombia.model.user.User;

/**
 * Interfaz común para todas las fábricas de estrategias
 */
public interface StrategyUserFactory {
    
    /**
     * Obtiene una estrategia por su tipo
     */
    CrearStrategy<User> getStrategy(CrearStrategyEnum type);
}

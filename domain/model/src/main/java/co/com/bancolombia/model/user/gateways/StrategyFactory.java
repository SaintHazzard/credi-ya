package co.com.bancolombia.model.user.gateways;


/**
 * Interfaz común para todas las fábricas de estrategias
 */
public interface StrategyFactory {
    
    /**
     * Obtiene una estrategia por su tipo
     */
    CrearUsuarioStrategy getStrategy(CrearUserStrategyEnum type);
}

package co.com.bancolombia.usecase.creacionuser.creacion;

/**
 * Interfaz común para todas las fábricas de estrategias
 */
public interface StrategyFactory {
    
    /**
     * Obtiene una estrategia por su tipo
     */
    CrearUsuarioStrategy getStrategy(CrearUserStrategyEnum type);
}

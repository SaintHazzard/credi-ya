package co.com.bancolombia.model.user.logger;

/**
 * Puerto para servicios de logging en el dominio
 */
public interface LoggingPort {
    
    /**
     * Registra un mensaje informativo
     */
    void info(String message);
    
    /**
     * Registra un mensaje de error
     */
    void error(String message, Throwable error);
    
    /**
     * Registra un mensaje de depuración
     */
    void debug(String message);
}

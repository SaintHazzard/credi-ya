package co.com.bancolombia.model.log;

/**
 * Puerto para el servicio de logging
 * Define las operaciones de logging que pueden ser usadas por los casos de uso
 */
public interface LoggerPort {
    
    /**
     * Registra un mensaje de información
     * @param clazz Clase desde donde se genera el log
     * @param message Mensaje a registrar
     * @param args Argumentos para el mensaje
     */
    void info(Class<?> clazz, String message, Object... args);
    
    /**
     * Registra un mensaje de depuración
     * @param clazz Clase desde donde se genera el log
     * @param message Mensaje a registrar
     * @param args Argumentos para el mensaje
     */
    void debug(Class<?> clazz, String message, Object... args);
    
    /**
     * Registra un mensaje de error
     * @param clazz Clase desde donde se genera el log
     * @param message Mensaje a registrar
     * @param error Excepción asociada al error
     * @param args Argumentos para el mensaje
     */
    void error(Class<?> clazz, String message, Throwable error, Object... args);
    
    /**
     * Registra un mensaje de advertencia
     * @param clazz Clase desde donde se genera el log
     * @param message Mensaje a registrar
     * @param args Argumentos para el mensaje
     */
    void warn(Class<?> clazz, String message, Object... args);

    /**
     * Registra el inicio de un método
     * @param clazz Clase del método
     * @param methodName Nombre del método
     * @param args Argumentos del método
     */
    void methodEntry(Class<?> clazz, String methodName, Object... args);
    
    /**
     * Registra la salida exitosa de un método
     * @param clazz Clase del método
     * @param methodName Nombre del método
     * @param executionTime Tiempo de ejecución en ms
     * @param result Resultado del método (opcional)
     */
    void methodExit(Class<?> clazz, String methodName, long executionTime, Object result);
    
    /**
     * Registra un error en un método
     * @param clazz Clase del método
     * @param methodName Nombre del método
     * @param executionTime Tiempo de ejecución en ms
     * @param error Error producido
     */
    void methodError(Class<?> clazz, String methodName, long executionTime, Throwable error);
}

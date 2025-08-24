package co.com.bancolombia.r2dbc.config.logs;

import co.com.bancolombia.model.log.LoggerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Implementación del puerto de logging utilizando SLF4J
 */
@Component
public class LogStandarUseCases implements LoggerPort {

    @Override
    public void info(Class<?> clazz, String message, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info(message, args);
    }

    @Override
    public void debug(Class<?> clazz, String message, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.debug(message, args);
    }

    @Override
    public void error(Class<?> clazz, String message, Throwable error, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.error(message, args, error);
    }

    @Override
    public void warn(Class<?> clazz, String message, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.warn(message, args);
    }

    @Override
    public void methodEntry(Class<?> clazz, String methodName, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("Entrando al método: {} con parámetros: {}", 
                methodName, 
                Arrays.toString(args));
    }

    @Override
    public void methodExit(Class<?> clazz, String methodName, long executionTime, Object result) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("Saliendo del método: {} - tiempo de ejecución: {} ms - resultado: {}", 
                methodName, 
                executionTime,
                result);
    }

    @Override
    public void methodError(Class<?> clazz, String methodName, long executionTime, Throwable error) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.error("Error en el método: {} - tiempo de ejecución: {} ms - error: {}", 
                methodName, 
                executionTime,
                error.getMessage(),
                error);
    }
}

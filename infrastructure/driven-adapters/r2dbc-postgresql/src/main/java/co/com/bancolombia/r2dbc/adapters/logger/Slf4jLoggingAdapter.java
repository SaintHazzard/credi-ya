package co.com.bancolombia.r2dbc.adapters.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.bancolombia.model.user.logger.LoggingPort;

/**
 * Implementación de LoggingPort usando SLF4J
 * Esta clase actúa como un adaptador entre nuestro dominio y la biblioteca de logging
 */
public class Slf4jLoggingAdapter implements LoggingPort {
    
    private final Logger logger = LoggerFactory.getLogger(Slf4jLoggingAdapter.class);
    
    @Override
    public void info(String message) {
        logger.info(message);
    }
    
    @Override
    public void error(String message, Throwable error) {
        logger.error(message, error);
    }
    
    @Override
    public void debug(String message) {
        logger.debug(message);
    }
}

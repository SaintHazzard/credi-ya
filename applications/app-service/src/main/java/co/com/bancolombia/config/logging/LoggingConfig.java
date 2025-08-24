package co.com.bancolombia.config.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.bancolombia.model.user.logger.LoggingPort;
import co.com.bancolombia.r2dbc.adapters.logger.Slf4jLoggingAdapter;

/**
 * Configuración para servicios de logging
 */
@Configuration
public class LoggingConfig {

    /**
     * Crea un bean de LoggingPort utilizando el adaptador SLF4J
     */
    @Bean
    public LoggingPort loggingPort() {
        return new Slf4jLoggingAdapter();
    }
}

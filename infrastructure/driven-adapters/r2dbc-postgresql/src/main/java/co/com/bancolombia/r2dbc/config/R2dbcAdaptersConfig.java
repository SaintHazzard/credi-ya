package co.com.bancolombia.r2dbc.config;

import co.com.bancolombia.model.user.gateways.PasswordEncoderPort;
import co.com.bancolombia.r2dbc.implementaciones.users.PasswordEncoderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class R2dbcAdaptersConfig {

    @Bean
    public PasswordEncoderPort passwordEncoder() {
        return new PasswordEncoderImpl();
    }
}

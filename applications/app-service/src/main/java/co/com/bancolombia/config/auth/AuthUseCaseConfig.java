package co.com.bancolombia.config.auth;

import co.com.bancolombia.model.user.gateways.PasswordEncoderPort;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.userCases.AuthUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {
    
    @Bean
    public AuthUseCase authUseCase(UserRepository userRepository, PasswordEncoderPort passwordEncoder) {
        return new AuthUseCase(userRepository, passwordEncoder);
    }
}

package co.com.bancolombia.api.config;

import co.com.bancolombia.api.auth.JwtProvider;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@Profile("test")
public class TestSecurityConfig {

  @Bean
  @Primary
  public JwtProvider jwtProvider() {
    JwtProvider mockProvider = Mockito.mock(JwtProvider.class);
    Mockito.when(mockProvider.validateToken(Mockito.anyString())).thenReturn(true);
    Mockito.when(mockProvider.extractUsername(Mockito.anyString())).thenReturn("test-user");
    Mockito.when(mockProvider.extractRoles(Mockito.anyString())).thenReturn(java.util.Arrays.asList("USER"));
    return mockProvider;
  }

  @Bean
  @Primary
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    // Configuración que permite todo el acceso, deshabilita la seguridad para pruebas
    return http
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
        .authorizeExchange(exchange -> exchange
            .pathMatchers("/**").permitAll())
        .build();
  }

}

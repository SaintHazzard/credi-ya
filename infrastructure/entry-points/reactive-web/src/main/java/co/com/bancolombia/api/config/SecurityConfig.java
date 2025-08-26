package co.com.bancolombia.api.config;

import co.com.bancolombia.api.auth.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    public SecurityConfig(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**", "/issuer/**", "/.well-known/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationWebFilter jwtAuthenticationFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(reactiveAuthenticationManager());
        filter.setServerAuthenticationConverter(serverWebExchange -> {
            String authHeader = serverWebExchange.getRequest().getHeaders().getFirst("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return Mono.just(new BearerToken(token));
            }
            
            return Mono.empty();
        });
        
        return filter;
    }
    
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return authentication -> {
            BearerToken token = (BearerToken) authentication;
            
            if (jwtProvider.validateToken(token.getCredentials())) {
                String username = jwtProvider.extractUsername(token.getCredentials());
                List<String> roles = jwtProvider.extractRoles(token.getCredentials());
                
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());
                
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                
                return Mono.just(auth);
            }
            
            return Mono.empty();
        };
    }
    
    public static class BearerToken implements Authentication {
        private final String token;
        private boolean authenticated = false;
        
        public BearerToken(String token) {
            this.token = token;
        }
        
        @Override
        public String getName() {
            return null;
        }
        
        @Override
        public String getCredentials() {
            return token;
        }
        
        @Override
        public Object getPrincipal() {
            return null;
        }
        
        @Override
        public boolean isAuthenticated() {
            return authenticated;
        }
        
        @Override
        public void setAuthenticated(boolean isAuthenticated) {
            this.authenticated = isAuthenticated;
        }
        
        @Override
        public List<SimpleGrantedAuthority> getAuthorities() {
            return List.of();
        }
        
        @Override
        public Object getDetails() {
            return null;
        }
    }
}

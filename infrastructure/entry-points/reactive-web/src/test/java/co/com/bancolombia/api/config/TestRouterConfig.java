package co.com.bancolombia.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@Profile("test")
public class TestRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> testRoutes() {
        return RouterFunctions
            .route()
            .GET("/api/**", request -> ServerResponse.ok()
                .header("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .header("Strict-Transport-Security", "max-age=31536000;")
                .header("X-Content-Type-Options", "nosniff")
                .header("Server", "")
                .header("Cache-Control", "no-store")
                .header("Pragma", "no-cache")
                .header("Referrer-Policy", "strict-origin-when-cross-origin")
                .build())
            .OPTIONS("/api/**", request -> ServerResponse.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .header("Strict-Transport-Security", "max-age=31536000;")
                .header("X-Content-Type-Options", "nosniff")
                .header("Server", "")
                .header("Cache-Control", "no-store")
                .header("Pragma", "no-cache")
                .header("Referrer-Policy", "strict-origin-when-cross-origin")
                .build())
            .build();
    }
}

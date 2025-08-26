package co.com.bancolombia.api.auth;

import co.com.bancolombia.usecase.userCases.AuthUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtProvider jwtProvider;
    private final AuthUseCase authUseCase;

    @Value("${jwt.client-id}")
    private String clientId;

    @Value("${jwt.issuer}")
    private String issuerUri;

    public AuthController(JwtProvider jwtProvider, AuthUseCase authUseCase) {
        this.jwtProvider = jwtProvider;
        this.authUseCase = authUseCase;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest request) {
        return authUseCase.authenticateUser(request.getUsername(), request.getPassword())
                .map(user -> {
                    // En un caso real, usaríamos los roles del usuario
                    if (request.getUsername().equals("admin1324")) {
                        String token = jwtProvider.generateToken(user.getUsername(), Arrays.asList("ADMIN", "USER"));
                        return ResponseEntity.ok(new AuthResponse(token));
                    }
                    String token = jwtProvider.generateToken(user.getUsername(), Arrays.asList("USER"));
                    return ResponseEntity.ok(new AuthResponse(token));
                })
                .onErrorResume(error -> Mono
                        .error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")));
    }

    @GetMapping("/validate")
    public Mono<ResponseEntity<Map<String, Object>>> validateToken(@RequestParam String token) {
        if (jwtProvider.validateToken(token)) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("username", jwtProvider.extractUsername(token));

            return Mono.just(ResponseEntity.ok(response));
        }

        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("valid", false, "error", "Invalid token")));
    }
}

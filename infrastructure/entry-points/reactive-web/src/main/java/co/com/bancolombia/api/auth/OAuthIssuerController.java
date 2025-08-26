package co.com.bancolombia.api.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/issuer")
public class OAuthIssuerController {

    @Value("${jwt.issuer}")
    private String issuer;
    
    @Value("${jwt.client-id}")
    private String clientId;
    
    @Value("${jwt.secret}")
    private String secret;

    @GetMapping("/.well-known/openid-configuration")
    public Mono<Map<String, Object>> openidConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("issuer", issuer);
        config.put("authorization_endpoint", issuer + "/authorize");
        config.put("token_endpoint", issuer + "/token");
        config.put("userinfo_endpoint", issuer + "/userinfo");
        config.put("jwks_uri", issuer + "/.well-known/jwks.json");
        config.put("response_types_supported", new String[]{"code", "token", "id_token"});
        config.put("grant_types_supported", new String[]{"authorization_code", "client_credentials", "password", "refresh_token"});
        config.put("subject_types_supported", new String[]{"public"});
        config.put("id_token_signing_alg_values_supported", new String[]{"HS256"});
        return Mono.just(config);
    }
    
    @GetMapping("/.well-known/jwks.json")
    public Mono<Map<String, Object>> jwksJson() {
        // En producción, deberías usar claves RSA/EC y no exponer la clave secreta
        // Esta es una implementación simplificada para la demo
        
        // Crear un kid único basado en el hash de la clave secreta
        String kid = "key-" + Math.abs(secret.hashCode());
        
        // Crear una representación de clave simétrica (para HS256)
        Map<String, Object> key = new HashMap<>();
        key.put("kid", kid);
        key.put("kty", "oct");  // Symmetric key
        key.put("alg", "HS256");
        key.put("use", "sig");
        
        // En un sistema real, no deberías exponer la clave secreta
        // Aquí solo exponemos un identificador, no la clave real
        key.put("k", Base64.getUrlEncoder().withoutPadding().encodeToString(
                ("jwks-identifier-" + kid).getBytes()));
        
        Map<String, Object> jwks = new HashMap<>();
        jwks.put("keys", new Object[]{key});
        return Mono.just(jwks);
    }
    
    @GetMapping
    public Mono<Map<String, Object>> issuerInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("issuer", issuer);
        info.put("client_id", clientId);
        return Mono.just(info);
    }
}

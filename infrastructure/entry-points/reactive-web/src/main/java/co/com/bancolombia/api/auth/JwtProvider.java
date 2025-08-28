package co.com.bancolombia.api.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties.Tomcat.Resource;
import org.springframework.stereotype.Component;

import co.com.bancolombia.api.config.JwtProperties;
import co.com.bancolombia.model.user.User;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtProvider {

    @Value("${jwt.expiration}")
    private long expirationSeconds;

    @Value("${jwt.issuer}")
    private String issuer;

    private final JwtProperties props;
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private boolean useRSA = false;

    public JwtProvider(JwtProperties props) {
        this.props = props;
        log.info("Private key length: {}", props.getPrivateKey().length());
        log.info("Public key length: {}", props.getPublicKey().length());

        if (props.getPrivateKey() != null && !props.getPrivateKey().isEmpty() &&
                props.getPublicKey() != null && !props.getPublicKey().isEmpty()) {
            try {
                this.privateKey = loadPrivateKey(props.getPrivateKey());
                this.publicKey = loadPublicKey(props.getPublicKey());
                this.useRSA = true;
            } catch (Exception e) {
                throw new RuntimeException("Error loading RSA keys", e);
            }
        } else {
            // Fallback a clave simétrica
            this.useRSA = true;
        }
    }

    @Value("${jwt.secret:}")
    private String secretB64;

    private RSAPublicKey loadPublicKey(String publicKeyPEM) throws NoSuchAlgorithmException, InvalidKeySpecException {
        publicKeyPEM = publicKeyPEM
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    private RSAPrivateKey loadPrivateKey(String privateKeyPEM)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        privateKeyPEM = privateKeyPEM
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    private SecretKey key() {

        byte[] bytes = Decoders.BASE64.decode(secretB64);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(User user, List<String> roles, String azp) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationSeconds);

        if (useRSA) {
            return Jwts.builder()
                    .subject(user.getUsername())
                    .issuer(issuer)
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(exp))
                    .claim("roles", roles)
                    .claim("azp", azp)
                    .claim("email", user.getEmail())
                    .signWith(privateKey, Jwts.SIG.RS256)
                    .compact();
        } else {
            return Jwts.builder()
                    .subject(user.getUsername())
                    .issuer(issuer)
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(exp))
                    .claim("roles", roles)
                    .claim("azp", azp)
                    .claim("email", user.getEmail())
                    .signWith(key(), Jwts.SIG.HS256)
                    .compact();
        }
    }

    public String generateToken(String username, List<String> roles, String azp) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationSeconds);

        if (useRSA) {
            return Jwts.builder()
                    .subject(username)
                    .issuer(issuer)
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(exp))
                    .claim("roles", roles)
                    .claim("azp", azp)
                    .signWith(privateKey, Jwts.SIG.RS256)
                    .compact();
        } else {
            return Jwts.builder()
                    .subject(username)
                    .issuer(issuer)
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(exp))
                    .claim("roles", roles)
                    .claim("azp", azp)
                    .signWith(key(), Jwts.SIG.HS256)
                    .compact();
        }
    }

    public boolean validateToken(String token) {
        try {
            if (useRSA) {
                Jwts.parser()
                        .requireIssuer(issuer)
                        .clockSkewSeconds(60)
                        .verifyWith(publicKey)
                        .build()
                        .parseSignedClaims(token);
            } else {
                Jwts.parser()
                        .requireIssuer(issuer)
                        .clockSkewSeconds(60)
                        .verifyWith(key())
                        .build()
                        .parseSignedClaims(token);
            }
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Map<String, Object> getJwkSet() {
        Map<String, Object> jwks = new HashMap<>();
        List<Map<String, Object>> keys = new java.util.ArrayList<>();

        if (useRSA) {
            // Configuración para RS256
            Map<String, Object> jwk = new HashMap<>();
            jwk.put("kty", "RSA");
            jwk.put("use", "sig");
            jwk.put("alg", "RS256");
            jwk.put("kid", "auth-key-id");

            // Exportar los componentes de la clave pública
            // Estos valores son específicos de la implementación RSA
            jwk.put("n", Base64.getUrlEncoder().withoutPadding().encodeToString(
                    publicKey.getModulus().toByteArray()));
            jwk.put("e", Base64.getUrlEncoder().withoutPadding().encodeToString(
                    publicKey.getPublicExponent().toByteArray()));

            keys.add(jwk);
        } else {
            // Configuración para HS256
            Map<String, Object> jwk = new HashMap<>();
            jwk.put("kty", "oct");
            jwk.put("use", "sig");
            jwk.put("kid", "key-" + Math.abs(secretB64.hashCode()));
            jwk.put("k", secretB64);
            jwk.put("alg", "HS256");

            keys.add(jwk);
        }

        jwks.put("keys", keys);
        return jwks;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> {
            Object rolesObj = claims.get("roles");
            if (rolesObj instanceof List) {
                return (List<String>) rolesObj;
            }
            return List.of();
        });
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        if (useRSA) {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } else {
            return Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
    }
}

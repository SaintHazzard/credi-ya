package co.com.bancolombia.api.auth;

import co.com.bancolombia.usecase.userCases.AuthUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@RequestMapping("/issuer")
public class OAuth2Controller {

  private final JwtProvider jwtProvider;
  private final AuthUseCase authUseCase;

  @Value("${jwt.client-id}")
  private String defaultClientId;

  @Value("${jwt.issuer}")
  private String issuerUri;

  @Value("${jwt.expiration}")
  private long expirationTime;

  // Una implementación real usaría almacenamiento persistente para los códigos de
  // autorización
  private final Map<String, AuthorizationRequest> authorizationCodes = new HashMap<>();
  private final Map<String, String> refreshTokens = new HashMap<>();

  public OAuth2Controller(JwtProvider jwtProvider, AuthUseCase authUseCase) {
    this.jwtProvider = jwtProvider;
    this.authUseCase = authUseCase;
  }

  /**
   * Endpoint de autorización para el flujo OAuth2 Authorization Code.
   */
  @GetMapping("/authorize")
  public Mono<ResponseEntity<?>> authorize(
      @RequestParam("response_type") String responseType,
      @RequestParam("client_id") String clientId,
      @RequestParam("redirect_uri") String redirectUri,
      @RequestParam(value = "scope", required = false) String scope,
      @RequestParam(value = "state", required = false) String state) {

    // Validar cliente y redirect URI (en una implementación real)
    if (!"code".equals(responseType)) {
      return Mono.just(ResponseEntity.badRequest().body("Unsupported response_type"));
    }

    // Generar código de autorización
    String authCode = UUID.randomUUID().toString();
    authorizationCodes.put(authCode, new AuthorizationRequest(clientId, redirectUri, scope, state));

    // Redirigir con el código
    String redirectUrl = redirectUri + "?code=" + authCode;
    if (state != null) {
      redirectUrl += "&state=" + state;
    }

    return Mono.just(ResponseEntity.status(HttpStatus.FOUND)
        .header("Location", redirectUrl)
        .build());
  }

  /**
   * Endpoint para obtener tokens (Authorization Code, Refresh Token, Client
   * Credentials)
   */
  @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public Mono<ResponseEntity<TokenResponse>> token(
      @RequestParam("grant_type") String grantType,
      @RequestParam(value = "code", required = false) String code,
      @RequestParam(value = "redirect_uri", required = false) String redirectUri,
      @RequestParam(value = "client_id", required = false) String clientId,
      @RequestParam(value = "client_secret", required = false) String clientSecret,
      @RequestParam(value = "refresh_token", required = false) String refreshToken,
      @RequestParam(value = "username", required = false) String username,
      @RequestParam(value = "password", required = false) String password) {

    // Validación básica de cliente (en una implementación real se haría más
    // completa)
    if (clientId == null) {
      clientId = defaultClientId;
    }

    switch (grantType) {
      case "authorization_code":
        return handleAuthorizationCodeGrant(code, redirectUri, clientId);
      case "refresh_token":
        return handleRefreshTokenGrant(refreshToken, clientId);
      case "password":
        return handlePasswordGrant(username, password, clientId);
      case "client_credentials":
        return handleClientCredentialsGrant(clientId, clientSecret);
      default:
        return Mono.just(ResponseEntity.badRequest().body(
            TokenResponse.errorResponse("unsupported_grant_type")));
    }
  }

  private Mono<ResponseEntity<TokenResponse>> handleAuthorizationCodeGrant(String code, String redirectUri,
      String clientId) {
    // Verificar código de autorización
    if (!authorizationCodes.containsKey(code)) {
      return Mono.just(ResponseEntity.badRequest().body(
          TokenResponse.errorResponse("invalid_grant")));
    }

    AuthorizationRequest authRequest = authorizationCodes.get(code);

    // Verificar que clientId y redirectUri coincidan con los del código
    if (!authRequest.getClientId().equals(clientId) || !authRequest.getRedirectUri().equals(redirectUri)) {
      return Mono.just(ResponseEntity.badRequest().body(
          TokenResponse.errorResponse("invalid_grant")));
    }

    // Eliminar código usado
    authorizationCodes.remove(code);

    // Generar tokens
    String accessToken = jwtProvider.generateToken("user_from_auth_code", Arrays.asList("USER"));
    String refreshToken = UUID.randomUUID().toString();
    refreshTokens.put(refreshToken, "user_from_auth_code");

    return Mono.just(ResponseEntity.ok(
        new TokenResponse(null, accessToken, refreshToken, expirationTime, authRequest.getScope())));
  }

  private Mono<ResponseEntity<TokenResponse>> handleRefreshTokenGrant(String refreshToken, String clientId) {
    // Verificar refresh token
    if (!refreshTokens.containsKey(refreshToken)) {
      return Mono.just(ResponseEntity.badRequest().body(
          TokenResponse.errorResponse("invalid_grant")));
    }

    String username = refreshTokens.get(refreshToken);

    // Generar nuevo access token
    String accessToken = jwtProvider.generateToken(username, Arrays.asList("USER"));

    return Mono.just(ResponseEntity.ok(
        new TokenResponse(null, accessToken, refreshToken, expirationTime, null)));
  }

  private Mono<ResponseEntity<TokenResponse>> handlePasswordGrant(String username, String password, String clientId) {
    // Autenticar usuario
    return authUseCase.authenticateUser(username, password)
        .map(user -> {
          // Generar tokens
          String accessToken = jwtProvider.generateToken(user.getUsername(), Arrays.asList("USER"));
          String refreshToken = UUID.randomUUID().toString();
          refreshTokens.put(refreshToken, user.getUsername());

          return ResponseEntity.ok(
              new TokenResponse(null, accessToken, refreshToken, expirationTime, "read write"));
        })
        .onErrorResume(error -> Mono.just(ResponseEntity.badRequest().body(
            TokenResponse.errorResponse("invalid_grant"))));
  }

  private Mono<ResponseEntity<TokenResponse>> handleClientCredentialsGrant(String clientId, String clientSecret) {
    // En una implementación real, validaríamos el clientId y clientSecret contra la
    // base de datos

    // Para esta demo, aceptamos un cliente predefinido
    if ("gateway-client".equals(clientId) && "gateway-client-secret".equals(clientSecret)) {
      // Generar access token para el cliente
      String accessToken = jwtProvider.generateToken(clientId, Arrays.asList("CLIENT"));

      return Mono.just(ResponseEntity.ok(
          new TokenResponse(null, accessToken, null, expirationTime, "read write")));
    }

    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
        TokenResponse.errorResponse("invalid_client")));
  }

  /**
   * Endpoint para obtener información del usuario
   */
  @GetMapping("/userinfo")
  public Mono<ResponseEntity<Map<String, Object>>> userInfo(@RequestHeader("Authorization") String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    String token = authHeader.substring(7);

    if (!jwtProvider.validateToken(token)) {
      return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    String username = jwtProvider.extractUsername(token);

    Map<String, Object> userInfo = new HashMap<>();
    userInfo.put("sub", username);
    userInfo.put("name", "User " + username);
    userInfo.put("email", username + "@example.com");

    return Mono.just(ResponseEntity.ok(userInfo));
  }
}

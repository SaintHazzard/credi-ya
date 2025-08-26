package co.com.bancolombia.api.auth;

import lombok.Data;

/**
 * Clase para almacenar información del código de autorización
 */

@Data
public class AuthorizationRequest {
  private final String clientId;
  private final String redirectUri;
  private final String scope;
  private final String state;

}

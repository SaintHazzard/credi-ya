package co.com.bancolombia.api.auth;

/**
 * Clase para la respuesta de token OAuth2
 */
public class TokenResponse {
    private final String tokenType;
    private final String accessToken;
    private final String refreshToken;
    private final long expiresIn;
    private final String scope;
    private final String error;

    public TokenResponse(String error, String accessToken, String refreshToken, long expiresIn, String scope) {
        this.tokenType = "bearer";
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.scope = scope;
        this.error = error;
    }

    // Constructor adicional para simplificar la creación
    public static TokenResponse errorResponse(String error) {
        return new TokenResponse(error, null, null, 0, null);
    }

    public String getToken_type() {
        return tokenType;
    }

    public String getAccess_token() {
        return accessToken;
    }

    public String getRefresh_token() {
        return refreshToken;
    }

    public long getExpires_in() {
        return expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public String getError() {
        return error;
    }
}

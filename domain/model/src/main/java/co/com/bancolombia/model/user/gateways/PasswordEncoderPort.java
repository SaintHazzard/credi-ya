package co.com.bancolombia.model.user.gateways;

public interface PasswordEncoderPort {
  String encode(String rawPassword);

  boolean matches(String rawPassword, String encodedPassword);
}

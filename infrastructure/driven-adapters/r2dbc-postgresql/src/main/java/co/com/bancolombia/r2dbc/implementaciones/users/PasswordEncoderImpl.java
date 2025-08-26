package co.com.bancolombia.r2dbc.implementaciones.users;

import co.com.bancolombia.model.user.gateways.PasswordEncoderPort;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Implementación temporal simplificada del PasswordEncoder.
 * En producción, debe usar BCryptPasswordEncoder de Spring Security.
 */
@Component
public class PasswordEncoderImpl implements PasswordEncoderPort {

  /**
   * Codifica la contraseña.
   * Esta es una implementación temporal para desarrollo.
   */
  @Override
  public String encode(String rawPassword) {
    // En una implementación real, usaríamos:
    return new BCryptPasswordEncoder().encode(rawPassword);
  }

  /**
   * Verifica si la contraseña coincide con la versión codificada.
   * Esta es una implementación temporal para desarrollo.
   */
  @Override
  public boolean matches(String rawPassword, String encodedPassword) {
    return new BCryptPasswordEncoder().matches(rawPassword, encodedPassword);
  }
}

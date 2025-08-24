package co.com.bancolombia.model.notification.gateways;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

/**
 * Puerto para el envío de notificaciones por email
 */
public interface EmailNotificationPort {
    
    /**
     * Envía un email de bienvenida a un usuario nuevo
     * @param user El usuario al que enviar el email
     * @return Mono con true si el envío fue exitoso, false en caso contrario
     */
    Mono<Boolean> sendWelcomeEmail(User user);
    
    /**
     * Envía un email de recuperación de contraseña
     * @param email Dirección de correo del usuario
     * @param resetToken Token para resetear la contraseña
     * @return Mono con true si el envío fue exitoso, false en caso contrario
     */
    Mono<Boolean> sendPasswordResetEmail(String email, String resetToken);
    
    /**
     * Envía una notificación personalizada
     * @param email Dirección de correo del destinatario
     * @param subject Asunto del email
     * @param body Cuerpo del mensaje
     * @return Mono con true si el envío fue exitoso, false en caso contrario
     */
    Mono<Boolean> sendCustomEmail(String email, String subject, String body);
}

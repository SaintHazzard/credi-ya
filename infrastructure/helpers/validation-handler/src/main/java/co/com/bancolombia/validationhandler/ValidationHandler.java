package co.com.bancolombia.validationhandler;

import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException; 
import jakarta.validation.Validator;

import reactor.core.publisher.Mono;

@Component
public class ValidationHandler {

  private final Validator validator;

  public ValidationHandler(Validator validator) {
    this.validator = validator;
  }

  /**
   * Valida un objeto reactivamente
   */
  public <T> Mono<T> validate(T object) {
    return Mono.fromCallable(() -> {
      Set<ConstraintViolation<T>> violations = validator.validate(object);
      if (!violations.isEmpty()) {
        throw new ConstraintViolationException(violations);
      }
      return object;
    });
  }

  /**
   * Valida un objeto con grupos específicos
   */
  public <T> Mono<T> validate(T object, Class<?>... groups) {
    return Mono.fromCallable(() -> {
      Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
      if (!violations.isEmpty()) {
        throw new ConstraintViolationException(violations);
      }
      return object;
    });
  }

  /**
   * Helper que combina parsing del body y validación
   */
  public <T> Mono<T> validateBody(ServerRequest request, Class<T> clazz) {
    return request.bodyToMono(clazz)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Body no puede estar vacío")))
        .flatMap(this::validate);
  }

  /**
   * Helper que combina parsing del body y validación con grupos
   */
  public <T> Mono<T> validateBody(ServerRequest request, Class<T> clazz, Class<?>... groups) {
    return request.bodyToMono(clazz)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Body no puede estar vacío")))
        .flatMap(body -> validate(body, groups));
  }
}

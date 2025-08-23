package co.com.bancolombia.exeptionhandler;

import org.springframework.stereotype.Component;
import org.springframework.web.server.WebExceptionHandler;

import jakarta.validation.ConstraintViolationException;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;

@Component
public class GlobalErrorWebExceptionHandler implements WebExceptionHandler {

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    if (ex instanceof ConstraintViolationException) {
      return handleConstraintViolation(exchange, (ConstraintViolationException) ex);
    }
    return Mono.error(ex); // Delegar a otros handlers
  }

  private Mono<Void> handleConstraintViolation(ServerWebExchange exchange,
      ConstraintViolationException ex) {
    // Manejo centralizado de errores de validación
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(HttpStatus.BAD_REQUEST);
    // ... resto de la lógica
    return response.setComplete();
  }
}

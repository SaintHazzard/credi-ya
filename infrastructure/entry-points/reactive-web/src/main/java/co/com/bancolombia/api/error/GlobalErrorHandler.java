package co.com.bancolombia.api.error;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.bancolombia.model.common.DomainException;
import co.com.bancolombia.model.common.ErrorCode;
import co.com.bancolombia.model.common.ValidationError.ValidationErrors;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Order(-2) // se ejecuta antes del default handler de Spring
public class GlobalErrorHandler implements ErrorWebExceptionHandler {
  private final ObjectMapper om;

  public GlobalErrorHandler(ObjectMapper om) {
    this.om = om;
  }

  @Override
  public @NonNull Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
    HttpStatus status = mapStatus(ex);
    Map<String, Object> body = toBody(ex, status);

    exchange.getResponse().setStatusCode(status);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

    try {
      byte[] bytes = om.writeValueAsBytes(body);
      return exchange.getResponse()
          .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    } catch (Exception e) {
      exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
      return exchange.getResponse().setComplete();
    }
  }

  private HttpStatus mapStatus(Throwable ex) {
    if (ex instanceof DomainException de) {
      return switch (de.getCode()) {
        case VALIDATION -> HttpStatus.BAD_REQUEST;
        case NOT_FOUND -> HttpStatus.NOT_FOUND;
        case DUPLICATE, CONFLICT -> HttpStatus.CONFLICT;
        case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
        case FORBIDDEN -> HttpStatus.FORBIDDEN;
        case TIMEOUT, DEPENDENCY_ERROR -> HttpStatus.BAD_GATEWAY;
        default -> HttpStatus.INTERNAL_SERVER_ERROR;
      };
    }
    if (ex instanceof WebExchangeBindException || ex instanceof ConstraintViolationException || 
        ex instanceof ValidationErrors) {
      return HttpStatus.BAD_REQUEST;
    }
    if (ex instanceof ResponseStatusException rse) {
      return HttpStatus.valueOf(rse.getStatusCode().value());
    }
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }

  private Map<String, Object> toBody(Throwable ex, HttpStatus status) {
    Map<String, Object> body = new HashMap<>();
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());

    if (ex instanceof DomainException de) {
      body.put("code", de.getCode().name());
      body.put("message", safeMessage(de.getMessage()));
      body.put("details", de.getDetails());
      return body;
    }

    if (ex instanceof WebExchangeBindException bex) {
      body.put("code", ErrorCode.VALIDATION.name());
      body.put("message", "Validation failed");
      body.put("fieldErrors", bex.getFieldErrors().stream()
          .map(this::fieldErrorToMap)
          .collect(Collectors.toList()));
      return body;
    }

    if (ex instanceof ConstraintViolationException cve) {
      body.put("code", ErrorCode.VALIDATION.name());
      body.put("message", "Validation failed");
      body.put("violations", cve.getConstraintViolations().stream()
          .map(this::violationToMap)
          .collect(Collectors.toList()));
      return body;
    }
    
    if (ex instanceof co.com.bancolombia.model.common.ValidationError.ValidationErrors validationErrors) {
      body.put("code", ErrorCode.VALIDATION.name());
      body.put("message", "Validation failed");
      body.put("fieldErrors", validationErrors.getErrors().stream()
          .map(this::validationErrorToMap)
          .collect(Collectors.toList()));
      return body;
    }

    body.put("code", ErrorCode.INTERNAL.name());
    body.put("message", safeMessage(ex.getMessage()));
    return body;
  }

  // ----- helpers null-safe -----

  private Map<String, Object> fieldErrorToMap(org.springframework.validation.FieldError fe) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("field", fe.getField());
    // rejectedValue puede ser null
    if (fe.getRejectedValue() != null) {
      m.put("rejectedValue", fe.getRejectedValue());
    }
    m.put("message", fe.getDefaultMessage());
    return m;
  }

  private Map<String, Object> violationToMap(ConstraintViolation<?> v) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("property", v.getPropertyPath() == null ? "" : v.getPropertyPath().toString());
    if (v.getInvalidValue() != null) {
      m.put("invalidValue", v.getInvalidValue());
    }
    m.put("message", v.getMessage());
    return m;
  }
  
  private Map<String, Object> validationErrorToMap(co.com.bancolombia.model.common.ValidationError error) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("field", error.getField());
    m.put("message", error.getMessage());
    return m;
  }

  private String safeMessage(String msg) {
    return (msg == null || msg.isBlank()) ? "Unexpected error" : msg;
  }
}

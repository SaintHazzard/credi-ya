package co.com.bancolombia.api.utilities;


import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import co.com.bancolombia.r2dbc.helper.utilities.ValidationHandler;
import reactor.core.publisher.Mono;

@Component
public class RequestBodyValidator {

  private final ValidationHandler validator;

  public RequestBodyValidator(ValidationHandler validator) {
    this.validator = validator;
  }

  public <T> Mono<T> readAndValidate(ServerRequest req, Class<T> clazz, Class<?>... groups) {
    return req.bodyToMono(clazz).flatMap(body -> validator.validate(body, groups));
  }
}

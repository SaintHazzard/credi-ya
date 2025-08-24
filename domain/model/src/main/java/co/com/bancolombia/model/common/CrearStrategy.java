package co.com.bancolombia.model.common;

import co.com.bancolombia.model.user.gateways.CrearStrategyEnum;
import reactor.core.publisher.Mono;

public interface CrearStrategy<T> {
  CrearStrategyEnum getType();
   Mono<T> create(T user);
}

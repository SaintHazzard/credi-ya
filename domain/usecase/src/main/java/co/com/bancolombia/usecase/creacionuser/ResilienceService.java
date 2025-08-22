package co.com.bancolombia.usecase.creacionuser;

import java.util.function.Supplier;

import reactor.core.publisher.Mono;

public interface ResilienceService {
   public <T> Mono<T> executeWithResilience(Supplier<Mono<T>> operation);
}

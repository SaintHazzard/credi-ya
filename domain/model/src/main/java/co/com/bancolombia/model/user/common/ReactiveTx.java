package co.com.bancolombia.model.user.common;


import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface ReactiveTx {
  <T> Mono<T> write(Supplier<Mono<T>> work);

  <T> Mono<T> read(Supplier<Mono<T>> work);

  
  default <T> Flux<T> writeMany(Supplier<Publisher<T>> work) {
    return Flux.from(work.get());
  }
}

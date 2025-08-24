package co.com.bancolombia.r2dbc.config;

import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.springframework.lang.NonNull;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.reactive.TransactionCallback;
import org.springframework.transaction.reactive.TransactionalOperator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Tx {
  private final TransactionalOperator writeOp;
  private final TransactionalOperator readOp;

  public Tx(TransactionalOperator writeOp, TransactionalOperator readOp) {
    this.writeOp = writeOp;
    this.readOp = readOp;
  }

  // 1) Base
  public <T> Mono<T> writeOne(Supplier<? extends Mono<? extends T>> op) {
    return Mono.from(writeOp.execute(status -> {
      return toMono(op.get())
          .onErrorResume(e -> {
            status.setRollbackOnly();
            return Mono.error(e);
          });
    }));
  }

  // 2) From Publisher
  public <T> Mono<T> writeOneFromPublisher(Supplier<? extends Publisher<? extends T>> op) {
    return Mono.from(writeOp.execute(status -> {
      return Mono.from(toPub(op.get()))
          .onErrorResume(e -> {
            status.setRollbackOnly();
            return Mono.error(e);
          });
    }));
  }

  public <T> Flux<T> writeMany(Supplier<? extends Publisher<? extends T>> op) {
    return Flux.from(writeOp.execute((ReactiveTransaction s) -> toPub(op.get())));
  }

  // 4) Lectura como Mono
  public <T> Mono<T> readOne(Supplier<? extends Mono<? extends T>> op) {
    return Mono.from(readOp.execute(status -> toMono(op.get())));
  }

  @SuppressWarnings("unchecked")
  private static <T> Mono<T> toMono(Mono<? extends T> m) {
    return (Mono<T>) m;
  }

  @SuppressWarnings("unchecked")
  private static <T> Publisher<T> toPub(Publisher<? extends T> p) {
    return (Publisher<T>) p;
  }
}
package co.com.bancolombia.r2dbc.config;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.reactive.TransactionalOperator;

import co.com.bancolombia.model.user.common.ReactiveTx;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import org.reactivestreams.Publisher;


public class ReactiveTxSpring implements ReactiveTx {
  private final TransactionalOperator writeOperator;
  private final TransactionalOperator readOperator;

  public ReactiveTxSpring(
      @Qualifier("writeOperator") TransactionalOperator writeOperator,
      @Qualifier("readOperator") TransactionalOperator readOperator) {
    this.writeOperator = writeOperator;
    this.readOperator = readOperator;
  }

  @Override
  public <T> Mono<T> write(Supplier<Mono<T>> work) {
    return Mono.defer(work).as(writeOperator::transactional);
  }

  @Override
  public <T> Mono<T> read(Supplier<Mono<T>> work) {
    return Mono.defer(work).as(readOperator::transactional);
  }

  @Override
  public <T> Flux<T> writeMany(Supplier<Publisher<T>> work) {
    return Flux.defer(() -> Flux.from(work.get())).as(writeOperator::transactional);
  }
}

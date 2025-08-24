package co.com.bancolombia.r2dbc.config;

import java.util.function.Function;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Native approach to Spring's reactive transactions using the recommended patterns
 * directly from the Spring Framework documentation.
 */
@Component
@RequiredArgsConstructor
public class Txbrr {
    private final ReactiveTransactionManager transactionManager;
    
    /**
     * Executes an operation within a transaction using the native Spring approach.
     * This is the most direct way to use Spring's reactive transactions.
     *
     * @param <T> The type of the result
     * @param operation The function that takes a TransactionalOperator and returns a Publisher
     * @return The result of the operation
     */
    public <T> Mono<T> executeInTransaction(Function<TransactionalOperator, Publisher<T>> operation) {
        // Create a TransactionalOperator for this specific transaction
        TransactionalOperator rxtx = TransactionalOperator.create(transactionManager);
        
        // Execute the operation within the transaction context
        return Mono.from(operation.apply(rxtx));
    }
    
    /**
     * Executes a Mono operation within a transaction.
     *
     * @param <T> The type of the result
     * @param operation The operation to execute
     * @return The result of the operation
     */
    public <T> Mono<T> withTransaction(Supplier<Mono<T>> operation) {
        return executeInTransaction(rxtx -> 
            operation.get().as(rxtx::transactional)
        );
    }
    
    /**
     * Executes a Flux operation within a transaction.
     *
     * @param <T> The type of the elements
     * @param operation The operation to execute
     * @return The stream of results
     */
    public <T> Flux<T> withTransactionMany(Supplier<Flux<T>> operation) {
        return Flux.from(executeInTransaction(rxtx -> 
            operation.get().as(rxtx::transactional)
        ));
    }
    
    /**
     * A more idiomatic approach for specific use cases.
     * This matches Spring's documentation examples more closely.
     */
    public <T> Mono<T> doInTransaction(Mono<T> mono) {
        TransactionalOperator rxtx = TransactionalOperator.create(transactionManager);
        return mono.as(rxtx::transactional);
    }
    
    /**
     * For directly applying a transaction to an existing Flux.
     */
    public <T> Flux<T> doInTransactionMany(Flux<T> flux) {
        TransactionalOperator rxtx = TransactionalOperator.create(transactionManager);
        return flux.as(rxtx::transactional);
    }
}

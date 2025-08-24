package co.com.bancolombia.r2dbc.config.transancionalidad;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;

import co.com.bancolombia.model.user.common.ReactiveTx;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;

/**
 * Configuration for reactive transactions.
 * Sets up the transaction managers and operators needed for database operations.
 */
@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor
public class TransactionConfig {

    private final ConnectionFactory connectionFactory;
    
    @Bean
    public ReactiveTransactionManager transactionManager() {
        return new R2dbcTransactionManager(connectionFactory);
    }
    
    @Bean
    @Qualifier("writeOperator")
    public TransactionalOperator writeOp(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }

    @Bean
    @Qualifier("readOperator")
    public TransactionalOperator readOp(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }

    /**
     * Provides a ReactiveTx implementation that can be injected
     * into domain services for transaction management.
     */
    @Bean
    public ReactiveTx reactiveTx(
            @Qualifier("writeOperator") TransactionalOperator writeOperator,
            @Qualifier("readOperator") TransactionalOperator readOperator) {
        return new ReactiveTxSpring(writeOperator, readOperator);
    }
}

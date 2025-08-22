package co.com.bancolombia.config.user.resiliencia;

import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import co.com.bancolombia.usecase.creacionuser.ResilienceService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@Slf4j
public class ResilienceServiceImpl implements ResilienceService {

    @Override
    public <T> Mono<T> executeWithResilience(Supplier<Mono<T>> operation) {
        return operation.get()
                .doOnSubscribe(sub -> log.info("Starting resilient execution"))
                .delaySubscription(Duration.ofSeconds(3))
                .retryWhen(
                        Retry.fixedDelay(5, Duration.ofSeconds(2))
                                .doBeforeRetry(rs -> log.warn("Retrying after failure: {}", rs.failure().getMessage())))
                .doOnError(err -> log.error("All retries failed: {}", err.getMessage()));
    }
}

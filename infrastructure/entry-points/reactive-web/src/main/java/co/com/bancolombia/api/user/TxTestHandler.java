package co.com.bancolombia.api.user;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.common.ReactiveTx;
import co.com.bancolombia.usecase.creacionuser.creacion.DeletegateCrearUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class TxTestHandler {
  private final ReactiveTx tx; 
  private final DeletegateCrearUser delegate;


  public Mono<ServerResponse> bulkCreateTest(ServerRequest req) {
    User u1 = User.builder()
        .names("Alejandro25")
        .lastname("12132")
        .birthDate(LocalDate.of(1998, 5, 12))
        .address("Cra 15 #45-10, Bucaramanga")
        .phone("300123456733")
        .email("test@example.com")
        .salaryBase(BigDecimal.valueOf(4400000))
        .build();

    User u2 = User.builder()
        .names("Test User 2")
        .lastname("12133")
        .birthDate(LocalDate.of(1998, 5, 12))
        .address("Cra 15 #45-10, Bucaramanga")
        .phone("300123456734")
        .email("test@example.com")
        .salaryBase(BigDecimal.valueOf(4400000))
        .build();

    return tx.write(() -> delegate.createUser(u1)
        .then(delegate.createUser(u2))
        .then(Mono.just("ok")))
        .flatMap(nada -> ServerResponse.ok().bodyValue("OK"));
  }
}

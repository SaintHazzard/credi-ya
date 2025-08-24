package co.com.bancolombia.api.user;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

// import co.com.bancolombia.model.common.ReactiveTx;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.implementaciones.users.DelegateCrearUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class TxTestHandler {
  // private final ReactiveTx tx; 
  private final DelegateCrearUserService delegate;


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
        .email("test@example.com") // Cambiado el email para evitar error de unicidad
        .salaryBase(BigDecimal.valueOf(4400000))
        .build();

    // Utilizamos el método de servicio que maneja la transacción internamente
    return delegate.createTwoUsers(u1, u2)
        .then(ServerResponse.ok().bodyValue("OK"));
  }
}

package co.com.bancolombia.usecase.creacionuser.creacion;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

public interface CrearUsuarioStrategy {
  CrearUserStrategy getType();
   Mono<User> createUser(User user);
}

package co.com.bancolombia.api.user;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.creacionuser.ManagementUserUseCase;
import co.com.bancolombia.usecase.creacionuser.creacion.DeletegateCrearUser;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final DeletegateCrearUser delegate;
    private final ManagementUserUseCase managementUserUseCase;

    public Mono<ServerResponse> listeGetUser(ServerRequest serverRequest) {
        return managementUserUseCase.findUserById(serverRequest.pathVariable("id"))
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .onErrorResume(e -> ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> listenGetAllUsers(ServerRequest serverRequest) {
        return managementUserUseCase.findAllUsers()
                .collectList()
                .flatMap(users -> ServerResponse.ok().bodyValue(users))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> listeCreateUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(User.class)
                .flatMap(delegate::createUser)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
}

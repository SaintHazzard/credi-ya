package co.com.bancolombia.api.user;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.bancolombia.r2dbc.dtos.user.UserDTO;
import co.com.bancolombia.r2dbc.entities.userentity.UserMapper;
import co.com.bancolombia.r2dbc.helper.utilities.ValidationHandler;
import co.com.bancolombia.usecase.creacionuser.ManagementUserUseCase;
import co.com.bancolombia.usecase.creacionuser.creacion.DeletegateCrearUser;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final DeletegateCrearUser delegate;
    private final ManagementUserUseCase managementUserUseCase;
    private final ValidationHandler validationHandler;
    private final UserMapper userMapper;

    public Mono<ServerResponse> listenGetUser(ServerRequest serverRequest) {
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

    public Mono<ServerResponse> listenCreateUser(ServerRequest req) {
        return req.bodyToMono(UserDTO.class)
                .flatMap(validationHandler::validate)
                .map(userMapper::toDomain)
                .flatMap(delegate::createUser)
                .map(userMapper::toDto)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

}

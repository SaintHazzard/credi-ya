package co.com.bancolombia.api.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.bancolombia.r2dbc.dtos.user.UserDTO;
import co.com.bancolombia.r2dbc.entities.userentity.UserMapper;
import co.com.bancolombia.r2dbc.helper.utilities.ValidationHandler;
import co.com.bancolombia.usecase.creacionuser.ManagementUserUseCase;
import co.com.bancolombia.usecase.creacionuser.creacion.DeletegateCrearUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Handler for user-related HTTP requests.
 * Implements native Spring transactional operations for user data.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserHandler {

    private final DeletegateCrearUser delegate;
    
    private final ManagementUserUseCase managementUserUseCase;
    private final ValidationHandler validationHandler;
    private final UserMapper userMapper;

    /**
     * Retrieves a user by ID.
     * This is a read-only transactional operation using Spring's native transaction support.
     *
     * @param serverRequest The incoming server request
     * @return ServerResponse with the found user or 404 if not found
     */
    public Mono<ServerResponse> listenGetUser(ServerRequest serverRequest) {
        String userId = serverRequest.pathVariable("id");
        log.debug("Finding user with ID: {}", userId);
        
        return managementUserUseCase.findUserById(userId)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .onErrorResume(e -> {
                    log.error("Error retrieving user with ID {}: {}", userId, e.getMessage());
                    return ServerResponse.notFound().build();
                });
    }

    /**
     * Retrieves all users.
     * This is a read-only transactional operation using Spring's native transaction support.
     *
     * @param serverRequest The incoming server request
     * @return ServerResponse with all users or error if failed
     */
    public Mono<ServerResponse> listenGetAllUsers(ServerRequest serverRequest) {
        log.debug("Retrieving all users");
        
        return managementUserUseCase.findAllUsers()
                .collectList()
                .flatMap(users -> ServerResponse.ok().bodyValue(users))
                .onErrorResume(e -> {
                    log.error("Error retrieving all users: {}", e.getMessage());
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .bodyValue(e.getMessage());
                });
    }

    /**
     * Creates a new user.
     * This is a write transactional operation using Spring's native transaction support.
     *
     * @param req The incoming server request with user data
     * @return ServerResponse with the created user or error if validation fails
     */
    public Mono<ServerResponse> listenCreateUser(ServerRequest req) {
        log.debug("Processing user creation request");
        
        return req.bodyToMono(UserDTO.class)
                .flatMap(validationHandler::validate)
                .map(userMapper::toDomain)
                .flatMap(delegate::createUser)
                .map(userMapper::toDto)
                .flatMap(createdUser -> {
                    log.info("User created successfully with ID: {}", createdUser.getId());
                    return ServerResponse.status(HttpStatus.CREATED).bodyValue(createdUser);
                })
                .onErrorResume(e -> {
                    log.error("Error creating user: {}", e.getMessage());
                    return ServerResponse.badRequest().bodyValue(e.getMessage());
                });
    }
}

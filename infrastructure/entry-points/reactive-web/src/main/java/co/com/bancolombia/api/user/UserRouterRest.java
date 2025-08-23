package co.com.bancolombia.api.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.web.bind.annotation.RequestMethod;
import io.swagger.v3.oas.annotations.Operation;

@Configuration
public class UserRouterRest {

    @Bean("userRouterFunction")
    @RouterOperations({
            @RouterOperation(path = "/api/v1/users/{id}", method = RequestMethod.GET, beanClass = UserHandler.class, beanMethod = "listenGetUser", operation = @Operation(summary = "Obtener usuario por ID")),
            @RouterOperation(path = "/api/v1/users", method = RequestMethod.POST, beanClass = UserHandler.class, beanMethod = "listenCreateUser", operation = @Operation(summary = "Crear un usuario"))
    })
    public RouterFunction<ServerResponse> routerFunction(UserHandler userHandler) {
        return route(GET("/api/v1/users/{id}"), userHandler::listenGetUser)
                .andRoute(POST("/api/v1/users"), userHandler::listenCreateUser)
                .and(route(GET("/api/v1/users"), userHandler::listenGetAllUsers));
    }
}

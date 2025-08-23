package co.com.bancolombia.api.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRouterRest {
  @Bean("userRouterFunction")
  public RouterFunction<ServerResponse> routerFunction(UserHandler userHandler) {
        return route(GET("/api/users/{id}"), userHandler::listenGetUser)
                .andRoute(POST("/api/users"), userHandler::listenCreateUser)
                .and(route(GET("/api/users"), userHandler::listenGetAllUsers));
    }
}

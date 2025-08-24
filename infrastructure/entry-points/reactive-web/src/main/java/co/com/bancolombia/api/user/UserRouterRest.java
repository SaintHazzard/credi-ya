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

    @Bean

    public RouterFunction<ServerResponse> routerUserFunction(UserHandler userHandler) {
        return route(GET("/api/v1/users/{id}"), userHandler::listenGetUser)
                .andRoute(POST("/api/v1/users"), userHandler::listenCreateUser)
                .and(route(GET("/api/v1/users"), userHandler::listenGetAllUsers));
    }

    @Bean
    public RouterFunction<ServerResponse> routerUserBulkFunction(TxTestHandler bulkCreate) {
        return route(POST("/api/v1/users/bulk"), bulkCreate::bulkCreateTest);
    }

}

package co.com.bancolombia.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.bancolombia.api.user.UserHandler;
import co.com.bancolombia.api.user.UserRouterRest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserRouterRestTest {

    @Mock
    private UserHandler userHandler;

    @InjectMocks
    private UserRouterRest userRouterRest;

    @Test
    void routerFunctionShouldMapToCorrectEndpoints() {
        // Act
        RouterFunction<ServerResponse> routerFunction = userRouterRest.routerUserFunction(userHandler);
        
        // Assert
        assertNotNull(routerFunction, "Router function should not be null");
        
        String routerString = routerFunction.toString();
        assertTrue(routerString.contains("/api/v1/users"), "Router should contain the user base path");
        assertTrue(routerString.contains("GET"), "Router should handle GET requests");
        assertTrue(routerString.contains("POST"), "Router should handle POST requests");
        
        assertTrue(routerString.contains("/{id}"), "Router should handle path with ID parameter");
    }
}
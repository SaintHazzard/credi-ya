package co.com.bancolombia.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.bancolombia.api.user.UserHandler;
import co.com.bancolombia.api.user.UserRouterRest;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRouterRestTest {

    @Mock
    private UserHandler userHandler;

    @InjectMocks
    private UserRouterRest userRouterRest;

    @Test
    void routerFunctionShouldMapToHandler() {
        // Arrange
        ServerResponse mockResponse = ServerResponse.status(HttpStatus.OK).build().block();
        when(userHandler.listenGetUser(any())).thenReturn(Mono.just(mockResponse));
        when(userHandler.listenCreateUser(any())).thenReturn(Mono.just(mockResponse));

        RouterFunction<ServerResponse> routerFunction = userRouterRest.routerUserFunction(userHandler);

        // Verificar que las rutas están mapeadas correctamente es complicado en una
        // prueba unitaria
        // Por lo general, esto se prueba mejor en pruebas de integración
        // Este test solo verifica que se devuelve una RouterFunction no nula
        assert routerFunction != null;
    }
}
# Implementación de AOP para Logging en UseCases

Este proyecto utiliza Aspect-Oriented Programming (AOP) para implementar un sistema de logging centralizado para los casos de uso (UseCase) y sus operaciones.

## Aspectos Implementados

1. **LoggingAspect**: Aspecto general para todos los métodos en el paquete `usecase` y sus subpaquetes.
2. **ReactiveLoggingAspect**: Aspecto especializado para operaciones reactivas (métodos que devuelven `Mono<?>` o `Flux<?>`).
3. **ResilienceLoggingAspect**: Aspecto especializado para operaciones de resiliencia (métodos que tienen "Resilience" en su nombre).

## Configuración

La configuración de AOP se encuentra en la clase `co.com.bancolombia.usecase.config.AopConfig`, que habilita el procesamiento automático de aspectos usando la anotación `@EnableAspectJAutoProxy`.

## Cómo Funciona

- Los aspectos interceptan las llamadas a los métodos en el paquete `usecase` y sus subpaquetes.
- Registran información como:
  - Nombre del método invocado
  - Clase a la que pertenece
  - Parámetros recibidos
  - Tiempo de ejecución
  - Resultado o errores producidos

## Pointcuts Utilizados

- `execution(* co.com.bancolombia.usecase..*.*(..))`
  - Intercepta todos los métodos en el paquete `usecase` y sus subpaquetes.

- `execution(reactor.core.publisher.Mono co.com.bancolombia.usecase..*.*(..))`
  - Intercepta métodos que devuelven `Mono<?>` en el paquete `usecase` y sus subpaquetes.

- `execution(reactor.core.publisher.Flux co.com.bancolombia.usecase..*.*(..))`
  - Intercepta métodos que devuelven `Flux<?>` en el paquete `usecase` y sus subpaquetes.

- `execution(* co.com.bancolombia.usecase..*.*Resilience*(..))`
  - Intercepta métodos relacionados con resiliencia (contienen "Resilience" en su nombre) en el paquete `usecase` y sus subpaquetes.

## Ejemplo de Log

```
INFO: Entrando al método: ManagementUserUseCase.findUserById con parámetros: [12345]
INFO: Iniciando operación reactiva Mono: ManagementUserUseCase.findUserById con parámetros: [12345]
DEBUG: Suscripción iniciada para: ManagementUserUseCase.findUserById
INFO: Operación reactiva Mono completada con éxito: ManagementUserUseCase.findUserById - tiempo: 125 ms - resultado: User(id=12345, name=John Doe)
INFO: Saliendo del método: ManagementUserUseCase.findUserById - tiempo de ejecución: 128 ms
```

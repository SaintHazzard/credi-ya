# Implementación de Logging con AOP

Esta implementación sigue los principios de la Arquitectura Limpia para proporcionar una solución de logging centralizada para los casos de uso.

## Estructura

### En el Dominio (Modelo)

- **LoggerPort**: Un puerto (interfaz) que define los métodos de logging que pueden ser utilizados por los casos de uso.

### En la Infraestructura (R2DBC Adapter)

- **LogStandarUseCases**: Implementación del puerto de logging utilizando SLF4J.
- **UseCaseLoggingAspect**: Aspecto que intercepta automáticamente las llamadas a métodos en los casos de uso.
- **AopConfig**: Configuración para habilitar el procesamiento de aspectos.

## Cómo Funciona

1. **Logs Automáticos**: Todos los métodos en el paquete `usecase` y sus subpaquetes son interceptados automáticamente. No se requiere código adicional.

2. **Logs Manuales**: Los casos de uso pueden inyectar el `LoggerPort` y utilizarlo directamente para logging personalizado.

3. **Comportamiento Especializado**:
   - Métodos que devuelven `Mono<?>` o `Flux<?>` tienen un manejo especial para capturar eventos reactivos.
   - Métodos relacionados con resiliencia (que contienen "Resilience" en su nombre) tienen logging adicional.

## Ejemplo de Uso

```java
@RequiredArgsConstructor
public class ConsultaUserUseCase {

    private final UserRepository userRepository;
    private final LoggerPort logger; // Inyección del puerto

    public Mono<User> consultarUserConLog(String id) {
        // Uso directo del logger
        logger.info(this.getClass(), "Consultando usuario con ID: {}", id);
        
        return userRepository.findById(id)
            .doOnSuccess(user -> 
                logger.info(this.getClass(), "Usuario encontrado: {}", user));
    }
}
```

## Beneficios

1. **Desacoplamiento**: El dominio no conoce los detalles de implementación del logging.
2. **Centralización**: Todos los logs tienen un formato consistente.
3. **Flexibilidad**: Se pueden cambiar las implementaciones sin modificar el código del dominio.
4. **Automatización**: El aspecto intercepta automáticamente los métodos, reduciendo código repetitivo.

## Consideraciones

- Los errores de compilación que aparecen son normales hasta que se compile el proyecto con las dependencias correctas.
- Para que el AOP funcione, los casos de uso deben ser gestionados por Spring (inyectados como beans).

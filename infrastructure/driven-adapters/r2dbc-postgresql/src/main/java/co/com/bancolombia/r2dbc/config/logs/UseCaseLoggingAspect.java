package co.com.bancolombia.r2dbc.config.logs;

import co.com.bancolombia.model.log.LoggerPort;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Aspecto para interceptar y registrar las llamadas a métodos en los casos de uso
 */
@Aspect
@Component
public class UseCaseLoggingAspect {

    private final LoggerPort logger;

    public UseCaseLoggingAspect(LoggerPort logger) {
        this.logger = logger;
    }

    /**
     * Pointcut que selecciona todos los métodos en el paquete usecase y sus subpaquetes
     */
    @Pointcut("execution(* co.com.bancolombia.usecase..*.*(..))")
    public void useCaseMethodsPointcut() {
        // Pointcut method, no implementation required
    }

    /**
     * Advice para interceptar métodos que devuelven tipos no reactivos
     */
    @Around("useCaseMethodsPointcut() && !execution(reactor.core.publisher.Mono co.com.bancolombia.usecase..*.*(..)) && !execution(reactor.core.publisher.Flux co.com.bancolombia.usecase..*.*(..))")
    public Object logAroundUseCaseMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();
        
        // Registrar entrada del método
        logger.methodEntry(targetClass, methodName, joinPoint.getArgs());
        
        long start = System.currentTimeMillis();
        Object result;
        
        try {
            // Ejecutar el método original
            result = joinPoint.proceed();
            
            // Registrar salida exitosa
            long executionTime = System.currentTimeMillis() - start;
            logger.methodExit(targetClass, methodName, executionTime, result);
            
            return result;
        } catch (Exception e) {
            // Registrar errores
            long executionTime = System.currentTimeMillis() - start;
            logger.methodError(targetClass, methodName, executionTime, e);
            throw e;
        }
    }

    /**
     * Advice para métodos que devuelven Mono<?>
     */
    @Around("useCaseMethodsPointcut() && execution(reactor.core.publisher.Mono co.com.bancolombia.usecase..*.*(..))")
    public Object logAroundMonoMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();
        
        // Registrar entrada del método
        logger.methodEntry(targetClass, methodName, joinPoint.getArgs());
        
        long startTime = System.currentTimeMillis();
        
        try {
            Mono<?> result = (Mono<?>) joinPoint.proceed();
            
            return result
                .doOnSuccess(value -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logger.methodExit(targetClass, methodName, executionTime, value);
                })
                .doOnError(error -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logger.methodError(targetClass, methodName, executionTime, error);
                });
        } catch (Exception e) {
            // Capturar errores durante la creación del Mono
            long executionTime = System.currentTimeMillis() - startTime;
            logger.methodError(targetClass, methodName, executionTime, e);
            throw e;
        }
    }

    /**
     * Advice para métodos que devuelven Flux<?>
     */
    @Around("useCaseMethodsPointcut() && execution(reactor.core.publisher.Flux co.com.bancolombia.usecase..*.*(..))")
    public Object logAroundFluxMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();
        
        // Registrar entrada del método
        logger.methodEntry(targetClass, methodName, joinPoint.getArgs());
        
        long startTime = System.currentTimeMillis();
        
        try {
            Flux<?> result = (Flux<?>) joinPoint.proceed();
            
            return result
                .doOnComplete(() -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logger.methodExit(targetClass, methodName, executionTime, "Flux completed");
                })
                .doOnError(error -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logger.methodError(targetClass, methodName, executionTime, error);
                });
        } catch (Exception e) {
            // Capturar errores durante la creación del Flux
            long executionTime = System.currentTimeMillis() - startTime;
            logger.methodError(targetClass, methodName, executionTime, e);
            throw e;
        }
    }

    /**
     * Pointcut y advice especializados para operaciones de resiliencia
     */
    @Pointcut("execution(* co.com.bancolombia.usecase..*.*Resilience*(..))")
    public void resilienceOperationsPointcut() {
        // Pointcut method, no implementation required
    }
    
    @Around("resilienceOperationsPointcut()")
    public Object logAroundResilienceOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();
        
        logger.info(targetClass, "Iniciando operación de resiliencia: {}", methodName);
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info(targetClass, "Operación de resiliencia completada: {} - tiempo: {} ms", 
                    methodName, executionTime);
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error(targetClass, "Error en operación de resiliencia: {} - tiempo: {} ms", 
                    e, methodName, executionTime);
            throw e;
        }
    }
}

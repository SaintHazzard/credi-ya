// package co.com.bancolombia.config.user;

// import org.springframework.stereotype.Component;

// import lombok.extern.slf4j.Slf4j;

// @Aspect
// @Component
// @Slf4j
// public class LoggingAspect {

//   @Around("execution(* co.com.bancolombia.usecase..*(..))")
//   public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
//     System.out.println("Entrando a " + pjp.getSignature());
//     Object result = pjp.proceed();
//     System.out.println("Saliendo de " + pjp.getSignature());
//     return result;
//   }
// }

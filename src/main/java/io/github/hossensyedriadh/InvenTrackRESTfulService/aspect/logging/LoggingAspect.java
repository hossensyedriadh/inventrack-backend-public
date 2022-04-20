package io.github.hossensyedriadh.InvenTrackRESTfulService.aspect.logging;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private final static Logger logger = Logger.getLogger(LoggingAspect.class);

    @Pointcut("execution(* io.github.hossensyedriadh.InvenTrackRESTfulService.controller.*.*.*.*(..))")
    public void executeForAllControllers() {
    }

    @Before("executeForAllControllers()")
    public void incoming(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        logger.info("Incoming request -> " + methodSignature + ".");
    }

    @After("executeForAllControllers()")
    public void outgoing(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        logger.info("Outbound response -> " + methodSignature + ".");
    }

    @AfterReturning(pointcut = "executeForAllControllers()", returning = "object")
    public void onSuccess(JoinPoint joinPoint, Object object) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?> returnType = methodSignature.getReturnType();
        logger.info("Method: " + methodSignature + " | returned " + returnType + " | response: " + object + ".");
    }

    @AfterThrowing(pointcut = "executeForAllControllers()", throwing = "throwable")
    public void onFailure(JoinPoint joinPoint, Throwable throwable) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?> returnType = methodSignature.getReturnType();
        logger.error("Method: " + methodSignature + " | Return Type: " + returnType + " has thrown " + throwable + ".");
    }
}

package com.alex.demo.java.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Aspect
public class LogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("@annotation(Log)")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object doSurround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] objects = proceedingJoinPoint.getArgs();
        String name = proceedingJoinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();

        LOGGER.info("class: {}, function name: {}", proceedingJoinPoint.getTarget().getClass().getName(), name);
        LOGGER.info("in args: <{}>, out args: <{}>", objects, result);
        LOGGER.info("execute time: {}", System.currentTimeMillis() - startTime);

        return result;
    }
}

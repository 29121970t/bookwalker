package com.kruosant.bookwalker.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@Aspect
public class TimeServiceExecutionAspect {
  Logger logger = LoggerFactory.getLogger(TimeServiceExecutionAspect.class);

  @Pointcut("execution(* com.kruosant.bookwalker.services.*.*(..))")
  public void serviceMethods() {
  }

  @Around("serviceMethods()")
  public Object timeExecution(ProceedingJoinPoint jp) throws Throwable {
    Instant start = Instant.now();
    Object result = jp.proceed();
    logger.info(
        "Took {} ms to execute {}.{}{}.",
        Duration.between(start, Instant.now()).toMillis(),
        jp.getSignature().getDeclaringTypeName(),
        jp.getSignature().getName(),
        jp.getArgs()
    );
    return result;
  }
}

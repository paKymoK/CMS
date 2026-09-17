package com.takypok.core.config;

import java.lang.annotation.Annotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Aspect
@Component
public class ReactiveAspectController {
  @Around("execution(* com.takypok.*.controller.*.*(..))")
  public Object logIncomingRequest(ProceedingJoinPoint joinPoint) throws Throwable {
    Logger logger =
        LogManager.getLogger(
            AopProxyUtils.ultimateTargetClass(joinPoint.getThis())
                + "->"
                + joinPoint.getSignature().getName());
    try {
      Mono<?> retVal = (Mono<?>) joinPoint.proceed();

      return Mono.deferContextual(
          contextView -> {
            try {
              ServerHttpRequest request = contextView.get(ServerWebExchange.class).getRequest();
              logger.info(
                  "[Request from {}] {} {}",
                  request.getRemoteAddress(),
                  request.getMethod(),
                  request.getURI());
              logRequest(joinPoint, logger);
              return retVal;
            } catch (Exception ignored) {
              return retVal.doFinally(
                  signalType -> logger.info("[End] Error occurred when logging request"));
            }
          });
    } catch (ClassCastException ex) {
      try {
        Flux<?> retVal = (Flux<?>) joinPoint.proceed();
        return Flux.deferContextual(
            contextView -> {
              try {
                ServerHttpRequest request = contextView.get(ServerWebExchange.class).getRequest();
                logger.info(
                    "[Request from {}] {} {}",
                    request.getRemoteAddress(),
                    request.getMethod(),
                    request.getURI());
                logRequest(joinPoint, logger);
                return retVal;
              } catch (Exception e) {
                throw new RuntimeException();
              }
            });
      } catch (Exception e) {
        logger.warn("Failed to determined the request, will process the request normally");
        return joinPoint.proceed();
      }
    }
  }

  public void logRequest(ProceedingJoinPoint joinPoint, Logger logger) {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Annotation[][] annotationMatrix = methodSignature.getMethod().getParameterAnnotations();
    int index = -1;
    for (Annotation[] annotations : annotationMatrix) {
      index++;
      for (Annotation annotation : annotations) {
        if ((annotation instanceof RequestBody)) {
          Object requestBody = joinPoint.getArgs()[index];
          logger.info("Request Body: {}", requestBody);
        }
      }
    }
  }
}

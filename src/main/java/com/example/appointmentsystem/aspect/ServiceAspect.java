package com.example.appointmentsystem.aspect;

import com.example.appointmentsystem.dto.AuthResponseTemp;
import com.example.appointmentsystem.dto.LoginRequest;
import com.example.appointmentsystem.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class ServiceAspect {

    @Before("execution(* com.example.appointmentsystem.service.*.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            log.info("[LOG - BEFORE] Calling method: {} with args: {}",
                    joinPoint.getSignature(), formatArgs(args));
        } else {
            log.info("[LOG - BEFORE] Calling method: {}", joinPoint.getSignature());
        }
    }

    @AfterReturning(pointcut = "execution(* com.example.appointmentsystem.service.*.*(..))", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {
        log.debug("[LOG - AFTER] Method finished: {}", joinPoint.getSignature());
        log.debug("[LOG - AFTER] Result: {}", result);
    }

    @Around("execution(* com.example.appointmentsystem.service.*.*(..))")
    public Object benchmarkMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long end = System.currentTimeMillis();
            log.info("[BENCHMARK] {} executed in {} ms", joinPoint.getSignature(), (end - start));
            return result;
        } catch (Throwable ex) {
            long end = System.currentTimeMillis();
            log.warn("[BENCHMARK] {} failed in {} ms", joinPoint.getSignature(), (end - start), ex);
            throw ex;
        }
    }

    @AfterThrowing(pointcut = "execution(* com.example.appointmentsystem.service.*.*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        log.error("[EXCEPTION] Method: {} threw exception: {}", joinPoint.getSignature(), ex.getMessage(), ex);
    }

    private String formatArgs(Object[] args) {
        return Arrays.stream(args)
                .map(this::formatArg)
                .collect(Collectors.joining(", "));
    }

    private String formatArg(Object arg) {
        if (arg == null) {
            return "null";
        }
        if (arg instanceof LoginRequest req) {
            return "LoginRequest[email=" + req.email() + ", password=***]";
        }
        if (arg instanceof RegisterRequest req) {
            return "RegisterRequest[fullName=" + req.fullName()
                    + ", email=" + req.email()
                    + ", password=***"
                    + ", role=" + req.role() + "]";
        }
        if (arg instanceof AuthResponseTemp) {
            return "AuthResponseTemp[token=***]";
        }
        return arg.toString();
    }
}

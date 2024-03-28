package apizza.order.util.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("@within(Logging) || @annotation(Logging)")
    public Object aroundLoggingMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!(joinPoint.getSignature() instanceof MethodSignature sign)) {
            return joinPoint.proceed();
        }

        log.debug("{}.{}() - start. {}", sign.getDeclaringTypeName(), sign.getMethod().getName(), getParametersString(joinPoint, sign));
        Object result = joinPoint.proceed();
        log.debug("{}.{}() - end. {}", sign.getDeclaringTypeName(), sign.getMethod().getName(), result == null ? "" : "result: %s".formatted(result));

        return result;
    }

    private String getParametersString(ProceedingJoinPoint joinPoint, MethodSignature sign) {
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = sign.getMethod().getParameters();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            builder.append(parameters[i].getName())
                    .append(": ")
                    .append(args[i]);
            if (i != parameters.length - 1) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }
}

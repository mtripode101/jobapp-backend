package com.mtripode.jobapp.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Single aspect for logging controllers, facades and services.
 *
 * - Pointcut covers packages: controller, facade, service.
 * - Logs method entry, exit, exceptions and execution time.
 * - Masks sensitive fields configurable via logging.sensitive.fields.
 * - Honors @NoLogging on methods or classes.
 */
@Aspect
@Component
@Order(100)
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    /**
     * Comma-separated list of sensitive field names (lowercased).
     * Default: password,token,secret,email,phone,ssn
     */
    @Value("${logging.sensitive.fields:password,token,secret,email,phone,ssn}")
    private String sensitiveFieldsProperty;

    private final Set<String> sensitiveFields = new HashSet<>();

    @PostConstruct
    private void initializeSensitiveFields() {
        if (sensitiveFieldsProperty != null && !sensitiveFieldsProperty.isBlank()) {
            String[] parts = sensitiveFieldsProperty.split(",");
            for (String p : parts) {
                String trimmed = p.trim().toLowerCase(Locale.ROOT);
                if (!trimmed.isEmpty()) sensitiveFields.add(trimmed);
            }
        }
    }

    /**
     * Pointcut that intercepts beans in:
     * - com.mtripode.jobapp.controller..
     * - com.mtripode.jobapp.facade..
     * - com.mtripode.jobapp.service..
     *
     * Using within(...) keeps the scope narrow and reduces overhead.
     */
    @Around("within(com.mtripode.jobapp.controller..*) || within(com.mtripode.jobapp.facade..*) || within(com.mtripode.jobapp.service..*)")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        // Skip logging if @NoLogging is present on method or declaring class
        if (method.isAnnotationPresent(NoLogging.class) ||
                method.getDeclaringClass().isAnnotationPresent(NoLogging.class)) {
            return pjp.proceed();
        }

        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        String argsJson = serializeArguments(signature.getParameterNames(), pjp.getArgs());

        logger.info("ENTER {}.{}({})", className, methodName, argsJson);

        Instant start = Instant.now();
        try {
            Object result = pjp.proceed();
            long elapsedMs = Duration.between(start, Instant.now()).toMillis();
            Object bodyToLog = unwrapResponseEntity(result);
            logger.info("EXIT  {}.{} -> {} ({} ms)", className, methodName, serializeSafe(bodyToLog), elapsedMs);
            return result;
        } catch (Throwable t) {
            long elapsedMs = Duration.between(start, Instant.now()).toMillis();
            logger.error("ERROR {}.{} after {} ms: {}", className, methodName, elapsedMs, t.toString(), t);
            throw t;
        }
    }

    /**
     * If the result is a ResponseEntity, return its body for clearer logging.
     */
    private Object unwrapResponseEntity(Object result) {
        if (result instanceof ResponseEntity) {
            try {
                return ((ResponseEntity<?>) result).getBody();
            } catch (Exception e) {
                return "[unreadable ResponseEntity]";
            }
        }
        return result;
    }

    /**
     * Serialize method arguments to JSON while masking sensitive fields.
     */
    private String serializeArguments(String[] paramNames, Object[] args) {
        if (args == null || args.length == 0) return "";
        try {
            Map<String, Object> map = new LinkedHashMap<>();
            for (int i = 0; i < args.length; i++) {
                String name = (paramNames != null && i < paramNames.length && paramNames[i] != null)
                        ? paramNames[i] : "arg" + i;
                map.put(name, maskIfSensitive(name, args[i]));
            }
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return Arrays.toString(Arrays.stream(args).map(this::safeToString).toArray());
        }
    }

    /**
     * Serialize an object safely, masking sensitive fields when possible.
     */
    private String serializeSafe(Object obj) {
        if (obj == null) return "null";
        try {
            return objectMapper.writeValueAsString(maskSensitiveInObject(obj));
        } catch (JsonProcessingException e) {
            return safeToString(obj);
        }
    }

    /**
     * Try to convert POJO or Map to Map and mask sensitive keys.
     * If conversion fails, return a limited toString representation.
     */
    private Object maskSensitiveInObject(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Map) {
            Map<?, ?> original = (Map<?, ?>) obj;
            Map<Object, Object> copy = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : original.entrySet()) {
                String key = String.valueOf(entry.getKey());
                copy.put(key, maskIfSensitive(key, entry.getValue()));
            }
            return copy;
        }
        try {
            Map<?, ?> asMap = objectMapper.convertValue(obj, Map.class);
            Map<Object, Object> copy = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : asMap.entrySet()) {
                String key = String.valueOf(entry.getKey());
                copy.put(key, maskIfSensitive(key, entry.getValue()));
            }
            return copy;
        } catch (IllegalArgumentException ex) {
            return safeToString(obj);
        }
    }

    /**
     * Mask value if the key name matches configured sensitive fields.
     * For large collections or maps, return a size summary instead of full content.
     */
    private Object maskIfSensitive(String key, Object value) {
        if (key == null) return value;
        String lowerKey = key.toLowerCase(Locale.ROOT);
        for (String s : sensitiveFields) {
            if (lowerKey.contains(s)) {
                return "[FILTERED]";
            }
        }
        if (value instanceof String) {
            String s = (String) value;
            if (s.length() > 500) return s.substring(0, 500) + "...(truncated)";
        }
        if (value instanceof Collection) {
            return String.format("[Collection size=%d]", ((Collection<?>) value).size());
        }
        if (value instanceof Map) {
            return String.format("[Map size=%d]", ((Map<?, ?>) value).size());
        }
        return value;
    }

    /**
     * Safe fallback toString with length limit.
     */
    private String safeToString(Object o) {
        try {
            String s = String.valueOf(o);
            if (s.length() > 500) return s.substring(0, 500) + "...(truncated)";
            return s;
        } catch (Exception e) {
            return "[unserializable]";
        }
    }
}
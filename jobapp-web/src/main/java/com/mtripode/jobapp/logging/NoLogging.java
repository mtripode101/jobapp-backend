package com.mtripode.jobapp.logging;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to exclude methods or classes from LoggingAspect.
 *
 * Usage:
 *   @NoLogging
 *   public class SomeController { ... }
 *
 * or
 *
 *   @NoLogging
 *   public ResponseEntity<?> someMethod(...) { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoLogging { }
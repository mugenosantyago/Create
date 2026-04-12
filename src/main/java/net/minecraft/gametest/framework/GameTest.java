package net.minecraft.gametest.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Stub for removed GameTest annotation (MC 1.21.8 port) */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GameTest {
    String templateNamespace() default "";
    String template() default "";
    long timeoutTicks() default 100L;
    int setupTicks() default 0;
    String batch() default "defaultBatch";
    boolean required() default true;
    int rotationSteps() default 0;
    boolean skyAccess() default false;
    int maxAttempts() default 1;
    int requiredSuccesses() default 1;
}

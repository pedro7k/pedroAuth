package com.pedro.auth.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解，可作用于类和方法上
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MethodAuth {
}

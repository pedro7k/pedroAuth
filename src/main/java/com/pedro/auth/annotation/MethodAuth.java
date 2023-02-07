package com.pedro.auth.annotation;

import com.pedro.auth.common.enums.RoleRuleEnum;
import com.pedro.auth.common.enums.RuleLevelEnum;

import java.lang.annotation.*;

/**
 * 权限校验注解，可作用于类和方法上
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MethodAuth {

    RuleLevelEnum level() default RuleLevelEnum.NO_AUTH;

    String roles() default "";

    RoleRuleEnum roleRule() default RoleRuleEnum.NEED_ONE;
}

package com.frameworks.aop.log;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Aspectj 自动配置注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ServiceLogAspect.class, WebLogAspect.class})
public @interface EnableLogAspectj {
    // ~
}

package com.frameworks.aop.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 传递参数和打印日志信息的格式
 *
 * @author DINGXIUAN
 */
@Deprecated
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface LogInfo {

    /**
     * 对当前所注解方法的描述
     */
    String description();

}

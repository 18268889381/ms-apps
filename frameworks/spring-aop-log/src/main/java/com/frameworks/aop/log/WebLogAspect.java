package com.frameworks.aop.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * Web请求的日志
 * <p>
 * execution：用于匹配方法执行的连接点；
 * within：用于匹配指定类型内的方法执行；
 * this：用于匹配当前AOP代理对象类型的执行方法；注意是AOP代理对象的类型匹配，这样就可能包括引入接口也类型匹配；
 * target：用于匹配当前目标对象类型的执行方法；注意是目标对象的类型匹配，这样就不包括引入接口也类型匹配；
 * args：用于匹配当前执行的方法传入的参数为指定类型的执行方法；
 *
 * @author DINGXIUAN
 * @within：用于匹配所以持有指定注解类型内的方法；
 * @target：用于匹配当前目标对象类型的执行方法，其中目标对象持有指定的注解；
 * @args：用于匹配当前执行的方法传入的参数持有指定注解的执行；
 * @annotation：用于匹配当前执行方法持有指定注解的方法； bean：Spring AOP扩展的，AspectJ没有对于指示符，用于匹配特定名称的Bean对象的执行方法；
 * reference pointcut：表示引用其他命名切入点，只有@ApectJ风格支持，Schema风格不支持。
 * <p>
 * AspectJ切入点支持的切入点指示符还有： call、get、set、preinitialization、staticinitialization、
 * initialization、handler、adviceexecution、withincode、cflow、cflowbelow、if、@this、
 * @withincode；但Spring AOP目前不支持这些指示符，使用这些指示符将抛出IllegalArgumentException异常。
 * 这些指示符Spring AOP可能会在以后进行扩展。
 */
@Aspect
public class WebLogAspect {

    private static final String PACKAGE_NAME = "com.frameworks.aop.log";
//    private static final String PACKAGE_NAME = WebLogAspect.class.getPackage().getName();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 切入点表达式的语法格式:
     * execution([权限修饰符] [返回值类型] [简单类名/全类名] [方法名]([参数列表]))
     */
    @Pointcut("!execution(@" + PACKAGE_NAME + ".AspectjLogIgnore * *(..))"
            + " && @within(" + PACKAGE_NAME + ".AspectjControllerLog)"
            + " && execution(public * *(..))")
    public void webLog() {
        // ~
    }

    /**
     * 被注解的方法
     */
    @Pointcut("!execution(@" + PACKAGE_NAME + ".AspectjLogIgnore * *(..))"
            + " && @annotation(" + PACKAGE_NAME + ".AspectjControllerLog)")
    public void webLog2() {
        // ~
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    @Before("webLog() || webLog2()")
    public void doBefore(JoinPoint joinPoint) {
        print(joinPoint);
    }

//    @AfterReturning("webLog()")
//    public void doAfterReturning(JoinPoint joinPoint) {
//        // 处理完请求，返回内容
//        logger.info("WebLogAspect.doAfterReturning()");
//    }


    private void print(JoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        // 接收到请求，记录请求内容
        HttpServletRequest request = getRequest();
        sb.append("url: ").append(request.getRequestURL());
        sb.append(", method: ").append(request.getMethod());
        sb.append(", ip: ").append(request.getRemoteAddr());
        sb.append(", headers: ").append(AopTool.getRequestHeaders(request).toString());
        String params = AopTool.getRequestParameters(request).toString();
        sb.append(", params: ").append(params);
        sb.append("\n").append(joinPoint.getSignature().getDeclaringTypeName());
        sb.append(".").append(joinPoint.getSignature().getName());
        sb.append("(");
        AopTool.join(sb, ",", joinPoint.getArgs());
        sb.append(")");
//        sb.append("(").append(Arrays.toString(joinPoint.getArgs())).append(")");
        logger.info(sb.toString());
    }
}

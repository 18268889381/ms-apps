package com.frameworks.aop.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 服务的日志
 * <p>
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
public class ServiceLogAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ServiceLogAspect() {
        logger.info("创建ServiceLogAspect对象");
    }

    /**
     * 切入点表达式的语法格式:
     * execution([权限修饰符] [返回值类型] [简单类名/全类名] [方法名]([参数列表]))
     * <p>
     * 被ServiceAspectjLog注解注释的所有类的public方法
     *
     * @within(com.framework.spring.aop.log.AspectjIgnore)
     */
    @Pointcut("!execution(@com.framework.mybatis.spring.aop.log.AspectjLogIgnore * *(..))"
            + " && @within(com.framework.mybatis.spring.aop.log.AspectjServiceLog)"
            + " && execution(public * *(..))")
    public void serviceLog() {
        // ~
    }

    /**
     * 被注解的方法
     */
    @Pointcut("!execution(@com.framework.mybatis.spring.aop.log.AspectjLogIgnore * *(..))"
            + " && @annotation(com.framework.mybatis.spring.aop.log.AspectjServiceLog)")
    public void serviceLog2() {
        // ~
    }

    @Before("serviceLog() || serviceLog2()")
    public void doBefore(JoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        sb.append("执行Service方法: ");
        Signature signature = joinPoint.getSignature();
//         Class<?> clazz = signature.getDeclaringType();
//         Object[] args = joinPoint.getArgs();
//         Class<?>[] parameterTypes = new Class[args.length];
//         for (int i = 0; i < args.length; i++) {
//         parameterTypes[i] = args.getClass();
//         }
//         Method method = clazz.getMethod(signature.getName(), parameterTypes);

        // 接收到请求，记录请求内容
        sb.append(signature.getDeclaringTypeName());
        sb.append(".").append(signature.getName());
        sb.append("(");
        AopTool.join(sb, ",", joinPoint.getArgs());
        sb.append(")");
        logger.info(sb.toString());
    }

//    @AfterReturning("serviceLog()")
//    public void doAfterReturning(JoinPoint joinPoint) {
//        // 处理完请求，返回内容
//         logger.info("ServiceLogAspect.doAfterReturning()");
//    }

}

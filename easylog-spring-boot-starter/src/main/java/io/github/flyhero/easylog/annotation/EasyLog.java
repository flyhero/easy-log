package io.github.flyhero.easylog.annotation;

import java.lang.annotation.*;

/**
 * 日志记录注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EasyLog {

    /**
     * 多租户使用
     */
    String tenant() default "";

    /**
     * 操作者
     */
    String operator() default "";

    /**
     * 模块
     */
    String module() default "";

    /**
     * 操作类型：比如增删改查
     */
    String type() default "";

    /**
     * 关联的业务id
     */
    String bizNo() default "";

    /**
     * 成功模板
     */
    String success();

    /**
     * 失败模板
     */
    String fail() default "";


    /**
     * 记录更详细的
     */
    String detail() default "";

    /**
     * 记录条件 默认 true
     */
    String condition() default "";

}

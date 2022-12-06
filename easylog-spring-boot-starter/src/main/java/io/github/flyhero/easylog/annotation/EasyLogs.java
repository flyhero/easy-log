package io.github.flyhero.easylog.annotation;

import java.lang.annotation.*;

/**
 * @author qfwang666@163.com
 * @date 2022/9/5 15:08
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EasyLogs {
    EasyLog[] value();
}

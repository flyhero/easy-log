package com.github.flyhero.easylog.function;

/**
 * @author qfwang666@163.com
 * @date 2022/2/20 17:20
 */
public interface IFunctionService {

    /**
     * 执行函数
     *
     * @param functionName 函数名
     * @param value        参数
     * @return 执行结果
     */
    String apply(String functionName, String value);

    /**
     * 是否在拦截的方法执行前执行
     *
     * @param functionName 函数名
     * @return
     */
    boolean executeBefore(String functionName);
}

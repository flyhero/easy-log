package io.github.flyhero.easylog.function.impl;

import io.github.flyhero.easylog.function.CustomFunctionFactory;
import io.github.flyhero.easylog.function.ICustomFunction;
import io.github.flyhero.easylog.function.IFunctionService;

/**
 * 自定义函数的默认实现，增加一层是为了屏蔽底层与上层直接接触
 * @author qfwang666@163.com
 * @date 2022/2/20 17:20
 */
public class DefaultFunctionServiceImpl implements IFunctionService {

    private final CustomFunctionFactory customFunctionFactory;

    public DefaultFunctionServiceImpl(CustomFunctionFactory customFunctionFactory) {
        this.customFunctionFactory = customFunctionFactory;
    }

    @Override
    public String apply(String functionName, Object value) {
        ICustomFunction function = customFunctionFactory.getFunction(functionName);
        if (function == null) {
            return value.toString();
        }
        return function.apply(value);
    }

    @Override
    public boolean executeBefore(String functionName) {
        ICustomFunction function = customFunctionFactory.getFunction(functionName);
        return function != null && function.executeBefore();
    }
}

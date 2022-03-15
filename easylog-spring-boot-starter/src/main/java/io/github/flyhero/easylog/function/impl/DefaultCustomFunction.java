package io.github.flyhero.easylog.function.impl;

import io.github.flyhero.easylog.function.ICustomFunction;

/**
 * @author WangQingFei(qfwang666@163.com)
 * @date 2022/2/26 12:17
 */
public class DefaultCustomFunction implements ICustomFunction {
    @Override
    public boolean executeBefore() {
        return false;
    }

    @Override
    public String functionName() {
        return "defaultName";
    }

    @Override
    public String apply(String value) {
        return null;
    }
}

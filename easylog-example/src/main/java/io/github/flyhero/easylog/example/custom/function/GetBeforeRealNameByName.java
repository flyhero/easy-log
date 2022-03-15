package io.github.flyhero.easylog.example.custom.function;

import io.github.flyhero.easylog.function.ICustomFunction;
import org.springframework.stereotype.Component;

/**
 * @author WangQingFei(qfwang666@163.com)
 * @date 2022/2/26 14:38
 */
@Component
public class GetBeforeRealNameByName implements ICustomFunction {
    @Override
    public boolean executeBefore() {
        return true;
    }

    @Override
    public String functionName() {
        return "getBeforeRealNameByName";
    }

    @Override
    public String apply(String value) {
        return "easylog".equals(value) ? "good" : value;
    }
}

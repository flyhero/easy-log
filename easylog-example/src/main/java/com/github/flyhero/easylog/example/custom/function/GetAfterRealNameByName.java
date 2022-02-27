package com.github.flyhero.easylog.example.custom.function;

import com.github.flyhero.easylog.function.ICustomFunction;
import org.springframework.stereotype.Component;

/**
 * @author WangQingFei(qfwang666@163.com)
 * @date 2022/2/26 14:38
 */
@Component
public class GetAfterRealNameByName implements ICustomFunction {
    @Override
    public boolean executeBefore() {
        return false;
    }

    @Override
    public String functionName() {
        return "getAfterRealNameByName";
    }

    @Override
    public String apply(String value) {
        return "easylog-new".equals(value) ? "new" : value;
    }
}

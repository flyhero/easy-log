package io.github.flyhero.easylog.function;

import io.github.flyhero.easylog.context.ApplicationContextHolder;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义函数工厂
 */
public class CustomFunctionFactory {

    private static final Map<String, ICustomFunction> customFunctionMap = new HashMap<>();

//    public CustomFunctionFactory() {
//        this.register();
//    }

    public CustomFunctionFactory(List<ICustomFunction> customFunctions) {
        for (ICustomFunction customFunction : customFunctions) {
            customFunctionMap.put(customFunction.functionName(), customFunction);
        }
    }

    /**
     * 从spring容器中获取实现 {@link ICustomFunction} 接口的类
     */
    private void register() {
        Map<String, ICustomFunction> beansOfType = ApplicationContextHolder.getInstance().getBeansOfType(ICustomFunction.class);
        if (CollectionUtils.isEmpty(beansOfType)) {
            return;
        }
        beansOfType.values().forEach(iCustomFunction -> {
            customFunctionMap.put(iCustomFunction.functionName(), iCustomFunction);
        });
    }

    /**
     * 通过函数名获取对应自定义函数
     *
     * @param functionName 函数名
     * @return 自定义函数
     */
    public ICustomFunction getFunction(String functionName) {
        return customFunctionMap.get(functionName);
    }

}

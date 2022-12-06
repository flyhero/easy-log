package io.github.flyhero.easylog.function;

import com.google.common.collect.Lists;
import io.github.flyhero.easylog.constants.EasyLogConsts;
import io.github.flyhero.easylog.context.EasyLogCachedExpressionEvaluator;
import io.github.flyhero.easylog.exception.EasyLogException;
import io.github.flyhero.easylog.model.EasyLogOps;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author WangQingFei(qfwang666 @ 163.com)
 * @date 2022/3/1 9:46
 */
public class EasyLogParser implements BeanFactoryAware {

    /**
     * 实现BeanFactoryAware以获取容器中的 beanFactory对象,
     * 拿到beanFactory后便可以获取容器中的bean,用于Spel表达式的解析
     */
    private BeanFactory beanFactory;

    private static final Pattern PATTERN = Pattern.compile("\\{\\s*(\\w*)\\s*\\{(.*?)}}");

    @Autowired
    private IFunctionService customFunctionService;

    private final EasyLogCachedExpressionEvaluator cachedExpressionEvaluator = new EasyLogCachedExpressionEvaluator();

    public Map<String, String> processAfterExec(List<String> expressTemplate, Map<String, String> funcValBeforeExecMap, Method method, Object[] args, Class<?> targetClass, String errMsg, Object result) {
        HashMap<String, String> map = new HashMap<>();
        AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
        EvaluationContext evaluationContext = cachedExpressionEvaluator.createEvaluationContext(method, args, beanFactory, errMsg, result);
        for (String template : expressTemplate) {
            if (template.contains("{")) {
                Matcher matcher = PATTERN.matcher(template);
                StringBuffer parsedStr = new StringBuffer();
                //匹配到字符串中的 {*{*}}
                while (matcher.find()) {
                    Object value = cachedExpressionEvaluator.parseExpression(matcher.group(2), elementKey, evaluationContext);
                    String funcName = matcher.group(1);
                    String param = value == null ? "" : value.toString();
                    String functionVal = ObjectUtils.isEmpty(funcName) ? param : getFunctionVal(funcValBeforeExecMap, funcName, param);
                    // 将 functionVal 替换 {*{*}}，然后将 从头截至到 匹配的最后字符 之间的字符串，放入 parsedStr 中
                    matcher.appendReplacement(parsedStr, functionVal);
                }
                // 将 从匹配的最后字符到整个字符串最后 之间的字符串，追加到parsedStr中
                matcher.appendTail(parsedStr);
                map.put(template, parsedStr.toString());
            } else {
                Object value = null;
                try {
                    value = cachedExpressionEvaluator.parseExpression(template, elementKey, evaluationContext);
                } catch (Exception e) {
                    throw new EasyLogException(method.getDeclaringClass().getName() + "." + method.getName() + "下 EasyLog 解析失败: [" + template + "], 请检查是否符合SpEl表达式规范！");
                }
                map.put(template, value == null ? "" : value.toString());
            }
        }
        return map;
    }


    /**
     * 获取执行前的自定义函数值
     *
     * @return
     */
    public Map<String, String> processBeforeExec(List<String> templates, Method method, Object[] args, Class<?> targetClass) {
        HashMap<String, String> map = new HashMap<>();
        AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
        EvaluationContext evaluationContext = cachedExpressionEvaluator.createEvaluationContext(method, args, beanFactory, null, null);
        for (String template : templates) {
            if (!template.contains("{")) {
                continue;
            }
            Matcher matcher = PATTERN.matcher(template);
            while (matcher.find()) {
                String param = matcher.group(2);
                if (param.contains(EasyLogConsts.POUND_KEY + EasyLogConsts.ERR_MSG) || param.contains(EasyLogConsts.POUND_KEY + EasyLogConsts.RESULT)) {
                    continue;
                }
                String funcName = matcher.group(1);
                if (customFunctionService.executeBefore(funcName)) {
                    Object value = cachedExpressionEvaluator.parseExpression(param, elementKey, evaluationContext);
                    String apply = customFunctionService.apply(funcName, value == null ? null : value.toString());
                    map.put(getFunctionMapKey(funcName, param), apply);
                }
            }
        }
        return map;
    }

    /**
     * 获取前置函数映射的 key
     *
     * @param funcName
     * @param param
     * @return
     */
    private String getFunctionMapKey(String funcName, String param) {
        return funcName + param;
    }


    /**
     * 获取自定义函数值
     *
     * @param funcValBeforeExecutionMap 执行之前的函数值
     * @param funcName                  函数名
     * @param param                     函数参数
     * @return
     */
    public String getFunctionVal(Map<String, String> funcValBeforeExecutionMap, String funcName, String param) {
        String val = null;
        if (!CollectionUtils.isEmpty(funcValBeforeExecutionMap)) {
            val = funcValBeforeExecutionMap.get(getFunctionMapKey(funcName, param));
        }
        if (ObjectUtils.isEmpty(val)) {
            val = customFunctionService.apply(funcName, param);
        }

        return val;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}

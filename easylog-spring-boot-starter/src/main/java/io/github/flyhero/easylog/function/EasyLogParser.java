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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public Map<String, String> process(EasyLogOps easyLogOps, Map<String, String> funcValBeforeExecMap, Method method, Object[] args, Class<?> targetClass, String errMsg, Object result) {
        HashMap<String, String> map = new HashMap<>();
        List<String> expressTemplate = getExpressTemplate(easyLogOps);
        AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
        EvaluationContext evaluationContext = cachedExpressionEvaluator.createEvaluationContext(method, args, beanFactory, errMsg, result);
        for (String template : expressTemplate) {
            if (template.contains("{")) {
                Matcher matcher = PATTERN.matcher(template);
                StringBuffer parsedStr = new StringBuffer();
                while (matcher.find()) {
                    Object value = cachedExpressionEvaluator.parseExpression(matcher.group(2), elementKey, evaluationContext);
                    String funcName = matcher.group(1);
                    String param = value == null ? "" : value.toString();
                    String functionVal = ObjectUtils.isEmpty(funcName) ? param : getFunctionVal(funcValBeforeExecMap, funcName, param);
                    matcher.appendReplacement(parsedStr, functionVal);
                }
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
    public Map<String, String> processBeforeExec(EasyLogOps easyLogOps,  Method method, Object[] args, Class<?> targetClass) {
        HashMap<String, String> map = new HashMap<>();
        AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
        EvaluationContext evaluationContext = cachedExpressionEvaluator.createEvaluationContext(method, args, beanFactory, null, null);
        List<String> templates = getExpressTemplate(easyLogOps);
        for (String template : templates) {
            if (template.contains("{")) {
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

    /**
     * 获取不为空的待解析模板
     *
     * @param easyLogOps
     * @return
     */
    private List<String> getExpressTemplate(EasyLogOps easyLogOps) {
        ArrayList<String> list = Lists.newArrayList(easyLogOps.getBizNo(), easyLogOps.getDetails(),
                easyLogOps.getOperator(), easyLogOps.getTenant(), easyLogOps.getSuccess(), easyLogOps.getFail(), easyLogOps.getCondition());
        return list.stream().filter(s -> !ObjectUtils.isEmpty(s)).collect(Collectors.toList());
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}

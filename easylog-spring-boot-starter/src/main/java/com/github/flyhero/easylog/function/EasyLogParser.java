package com.github.flyhero.easylog.function;

import com.github.flyhero.easylog.constants.EasyLogConsts;
import com.github.flyhero.easylog.model.EasyLogOps;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

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
@AllArgsConstructor
public class EasyLogParser {


    private static final Pattern PATTERN = Pattern.compile("\\{\\s*(\\w*)\\s*\\{(.*?)}}");

    private IFunctionService customFunctionService;

    /**
     * @param easyLogOps
     * @param funcValBeforeExecMap
     * @param evaluationContext
     * @return
     */
    public Map<String, String> process(EasyLogOps easyLogOps, Map<String, String> funcValBeforeExecMap, EvaluationContext evaluationContext) {
        HashMap<String, String> map = new HashMap<>();
        List<String> expressTemplate = getExpressTemplate(easyLogOps);
        ExpressionParser expressionParser = new SpelExpressionParser();
        for (String template : expressTemplate) {
            if (template.contains("{")) {
                Matcher matcher = PATTERN.matcher(template);
                StringBuffer parsedStr = new StringBuffer();
                while (matcher.find()) {
                    Object value = expressionParser.parseExpression(matcher.group(2)).getValue(evaluationContext, Object.class);
                    String funcName = matcher.group(1);
                    String param = value == null ? "" : value.toString();
                    String functionVal = ObjectUtils.isEmpty(funcName) ? param : getFunctionVal(funcValBeforeExecMap, funcName, param);
                    matcher.appendReplacement(parsedStr, functionVal);
                }
                matcher.appendTail(parsedStr);
                map.put(template, parsedStr.toString());
            } else {
                map.put(template, template);
            }
        }
        return map;
    }


    /**
     * 获取执行前的自定义函数值
     *
     * @param easyLogOps
     * @param evaluationContext
     * @return
     */
    public Map<String, String> processBeforeExec(EasyLogOps easyLogOps, EvaluationContext evaluationContext) {
        HashMap<String, String> map = new HashMap<>();
        ExpressionParser expressionParser = new SpelExpressionParser();
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
                        Object value = expressionParser.parseExpression(param).getValue(evaluationContext, Object.class);
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
        return funcName + EasyLogConsts.POUND_KEY + param;
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
                easyLogOps.getOperator(), easyLogOps.getTenant(), easyLogOps.getContent(), easyLogOps.getFail(), easyLogOps.getCondition());
        return list.stream().filter(s -> !ObjectUtils.isEmpty(s)).collect(Collectors.toList());
    }
}

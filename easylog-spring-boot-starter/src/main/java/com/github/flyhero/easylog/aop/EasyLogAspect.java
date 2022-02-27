package com.github.flyhero.easylog.aop;

import com.github.flyhero.easylog.annotation.EasyLog;
import com.github.flyhero.easylog.constants.VarConsts;
import com.github.flyhero.easylog.context.EasyLogEvaluationContext;
import com.github.flyhero.easylog.model.EasyLogOps;
import com.github.flyhero.easylog.model.EasyLogInfo;
import com.github.flyhero.easylog.function.IFunctionService;
import com.github.flyhero.easylog.service.ILogRecordService;
import com.github.flyhero.easylog.service.IOperatorService;
import com.github.flyhero.easylog.util.JsonUtils;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author qfwang666@163.com
 * @date 2022/2/20 17:28
 */
@Aspect
@Component
@AllArgsConstructor
public class EasyLogAspect {

    private ILogRecordService logRecordService;

    private IFunctionService customFunctionService;

    private IOperatorService operatorService;

    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.github.flyhero.easylog.annotation.EasyLog)")
    public void pointCut() {
    }

    /**
     * 环绕通知
     *
     * @param joinPoint
     * @param easyLog
     * @return
     */
    @Around("pointCut() && @annotation(easyLog)")
    public Object around(ProceedingJoinPoint joinPoint, EasyLog easyLog) throws Throwable {

        StopWatch stopWatch = new StopWatch("记录操作日志");
        stopWatch.start("执行前操作");
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();

        // 1. 方法参数添加到上下文
        EasyLogEvaluationContext evaluationContext = new EasyLogEvaluationContext(null, method, args, new DefaultParameterNameDiscoverer());
        EasyLogOps easyLogOps = parseLog(easyLog);
        List<String> expressTemplate = getExpressTemplate(easyLogOps);
        Map<String, String> map = getFuncValMapBeforeExec(expressTemplate, evaluationContext);

        long operatorDate= System.currentTimeMillis();
        Object result = null;
        boolean ok = true;
        String errMsg = null;
        stopWatch.stop();
        try {
            stopWatch.start("执行目标方法");
            result = joinPoint.proceed();
            stopWatch.stop();
        } catch (Throwable e) {
            stopWatch.stop();
            ok = false;
            errMsg = e.getMessage();
            evaluationContext.setVariable(VarConsts.ERR_MSG, errMsg);
            throw e;
        } finally {
            stopWatch.start("执行后操作");
            if (ok){
                evaluationContext.setVariable(VarConsts.RESULT, JsonUtils.toJSONString(result));
            }
            Map<String, String> templateMap = process(easyLogOps, map, evaluationContext);
            EasyLogInfo easyLogInfo = createLogRecord(templateMap, easyLogOps);
            if (Objects.nonNull(easyLogInfo)) {
                easyLogInfo.setContent(ok ? templateMap.get(easyLogOps.getContent()) : templateMap.get(easyLogOps.getFail()));
                easyLogInfo.setSuccess(ok);
                easyLogInfo.setResult(JsonUtils.toJSONString(result));
                easyLogInfo.setErrorMsg(errMsg);
                Long time = Arrays.stream(stopWatch.getTaskInfo())
                        .filter(taskInfo -> "执行目标方法".equals(taskInfo.getTaskName()))
                        .map(StopWatch.TaskInfo::getTimeMillis).findFirst().orElse(-1L);
                easyLogInfo.setExecutionTime(time);
                easyLogInfo.setOperateDate(operatorDate);
                logRecordService.record(easyLogInfo);
            }
            stopWatch.stop();
            System.out.println(stopWatch.prettyPrint());
        }
        return result;
    }

    private static final Pattern PATTERN = Pattern.compile("\\{\\s*(\\w*)\\s*\\{(.*?)}}");

    /**
     * @param easyLogOps
     * @param funcValBeforeExecMap
     * @param evaluationContext
     * @return
     */
    private Map<String, String> process(EasyLogOps easyLogOps, Map<String, String> funcValBeforeExecMap, EvaluationContext evaluationContext) {
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

    private EasyLogInfo createLogRecord(Map<String, String> templateMap, EasyLogOps easyLogOps) {
        //记录条件为 false，则不记录
        if ("false".equalsIgnoreCase(templateMap.get(easyLogOps.getCondition()))) {
            return null;
        }

        EasyLogInfo easyLogInfo = new EasyLogInfo();
        String tenant = templateMap.get(easyLogOps.getTenant());
        if (ObjectUtils.isEmpty(tenant)) {
            tenant = operatorService.getTenant();
        }
        easyLogInfo.setTenant(tenant);
        String operator = templateMap.get(easyLogOps.getOperator());
        if (ObjectUtils.isEmpty(operator)) {
            operator = operatorService.getOperator();
        }
        easyLogInfo.setModule(easyLogOps.getModule());
        easyLogInfo.setOperateType(easyLogOps.getOperateType());
        easyLogInfo.setOperator(operator);
        easyLogInfo.setBizNo(templateMap.get(easyLogOps.getBizNo()));
        easyLogInfo.setDetails(templateMap.get(easyLogOps.getDetails()));
        return easyLogInfo;
    }

    /**
     * 获取执行前的自定义函数值
     *
     * @param templates
     * @param evaluationContext
     * @return
     */
    private Map<String, String> getFuncValMapBeforeExec(List<String> templates, EvaluationContext evaluationContext) {
        HashMap<String, String> map = new HashMap<>();
        ExpressionParser expressionParser = new SpelExpressionParser();
        for (String template : templates) {
            if (template.contains("{")) {
                Matcher matcher = PATTERN.matcher(template);
                while (matcher.find()) {
                    String param = matcher.group(2);
                    if (param.contains(VarConsts.POUND_KEY + VarConsts.ERR_MSG) || param.contains(VarConsts.POUND_KEY + VarConsts.RESULT)) {
                        continue;
                    }
                    String funcName = matcher.group(1);
                    if (customFunctionService.executeBefore(funcName)) {
                        Object value = expressionParser.parseExpression(param).getValue(evaluationContext, Object.class);
                        String apply = customFunctionService.apply(funcName, value == null ? null : value.toString());
                        map.put(funcName, apply);
                    }
                }
            }
        }
        return map;
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
            val = funcValBeforeExecutionMap.get(funcName);
        }
        if (ObjectUtils.isEmpty(val)) {
            val = customFunctionService.apply(funcName, param);
        }

        return val;
    }

    /**
     * 将注解转为实体
     * @param easyLog
     * @return
     */
    private EasyLogOps parseLog(EasyLog easyLog) {
        EasyLogOps easyLogOps = new EasyLogOps();
        easyLogOps.setContent(easyLog.content());
        easyLogOps.setFail(easyLog.fail());
        easyLogOps.setModule(easyLog.module());
        easyLogOps.setOperateType(easyLog.operateType());
        easyLogOps.setOperator(easyLog.operator());
        easyLogOps.setBizNo(easyLog.bizNo());
        easyLogOps.setTenant(easyLog.tenant());
        easyLogOps.setDetails(easyLog.detail());
        easyLogOps.setCondition(easyLog.condition());
        return easyLogOps;
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

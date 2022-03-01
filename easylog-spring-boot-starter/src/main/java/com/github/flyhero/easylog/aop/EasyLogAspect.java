package com.github.flyhero.easylog.aop;

import com.github.flyhero.easylog.annotation.EasyLog;
import com.github.flyhero.easylog.constants.VarConsts;
import com.github.flyhero.easylog.context.EasyLogEvaluationContext;
import com.github.flyhero.easylog.function.EasyLogParser;
import com.github.flyhero.easylog.model.EasyLogOps;
import com.github.flyhero.easylog.model.EasyLogInfo;
import com.github.flyhero.easylog.function.IFunctionService;
import com.github.flyhero.easylog.service.ILogRecordService;
import com.github.flyhero.easylog.service.IOperatorService;
import com.github.flyhero.easylog.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author qfwang666@163.com
 * @date 2022/2/20 17:28
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class EasyLogAspect {

    private ILogRecordService logRecordService;

    private IFunctionService customFunctionService;

    private IOperatorService operatorService;

    private EasyLogParser easyLogParser;

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
        EasyLogOps easyLogOps = parseLogAnnotation(easyLog);

        Map<String, String> map = easyLogParser.processBeforeExec(easyLogOps, evaluationContext);

        long operatorDate = System.currentTimeMillis();
        Object result = null;
        boolean success = true;
        String errMsg = null;
        stopWatch.stop();
        try {
            stopWatch.start("执行目标方法");
            result = joinPoint.proceed();
            stopWatch.stop();
        } catch (Throwable e) {
            stopWatch.stop();
            success = false;
            errMsg = e.getMessage();
            evaluationContext.setVariable(VarConsts.ERR_MSG, errMsg);
            throw e;
        } finally {
            stopWatch.start("执行后操作");
            evaluationContext.setVariable(VarConsts.RESULT, JsonUtils.toJSONString(result));

            if (!success && ObjectUtils.isEmpty(easyLogOps.getFail())) {
                log.error("[{}] 方法执行失败，EasyLog 失败模板没有配置", method.getName());
            }else {
                Map<String, String> templateMap = easyLogParser.process(easyLogOps, map, evaluationContext);
                EasyLogInfo easyLogInfo = createEasyLogInfo(templateMap, easyLogOps);
                if (Objects.nonNull(easyLogInfo)) {
                    easyLogInfo.setContent(success ? templateMap.get(easyLogOps.getContent()) : templateMap.get(easyLogOps.getFail()));
                    easyLogInfo.setSuccess(success);
                    easyLogInfo.setResult(JsonUtils.toJSONString(result));
                    easyLogInfo.setErrorMsg(errMsg);
                    Long time = Arrays.stream(stopWatch.getTaskInfo())
                            .filter(taskInfo -> "执行目标方法".equals(taskInfo.getTaskName()))
                            .map(StopWatch.TaskInfo::getTimeMillis).findFirst().orElse(-1L);
                    easyLogInfo.setExecutionTime(time);
                    easyLogInfo.setOperateDate(operatorDate);
                    logRecordService.record(easyLogInfo);
                }
            }

            stopWatch.stop();
            System.out.println(stopWatch.prettyPrint());
        }
        return result;
    }

    /**
     * 创建操作日志实体
     *
     * @param templateMap
     * @param easyLogOps
     * @return
     */
    private EasyLogInfo createEasyLogInfo(Map<String, String> templateMap, EasyLogOps easyLogOps) {
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
     * 将注解转为实体
     *
     * @param easyLog
     * @return
     */
    private EasyLogOps parseLogAnnotation(EasyLog easyLog) {
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

}

package io.github.flyhero.easylog.aop;

import io.github.flyhero.easylog.annotation.EasyLog;
import io.github.flyhero.easylog.context.EasyLogEvaluationContext;
import io.github.flyhero.easylog.function.EasyLogParser;
import io.github.flyhero.easylog.model.EasyLogOps;
import io.github.flyhero.easylog.model.EasyLogInfo;
import io.github.flyhero.easylog.model.MethodExecuteResult;
import io.github.flyhero.easylog.service.ILogRecordService;
import io.github.flyhero.easylog.service.IOperatorService;
import io.github.flyhero.easylog.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

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

    private IOperatorService operatorService;

    private EasyLogParser easyLogParser;

    /**
     * 定义切点
     */
    @Pointcut("@annotation(io.github.flyhero.easylog.annotation.EasyLog)")
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

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        Class<?> targetClass = AopUtils.getTargetClass(target);

        EasyLogOps easyLogOps = parseLogAnnotation(easyLog);
        Map<String, String> map = easyLogParser.processBeforeExec(easyLogOps, method, args, targetClass);

        Object result = null;
        MethodExecuteResult executeResult = new MethodExecuteResult(true);
        try {
            result = joinPoint.proceed();
            executeResult.calcExecuteTime();
        } catch (Throwable e) {
            executeResult.exception(e);
        }
//        evaluationContext.putResult(executeResult.getErrMsg(), result);

        if (!executeResult.isSuccess() && ObjectUtils.isEmpty(easyLogOps.getFail())) {
            log.warn("[{}] 方法执行失败，EasyLog 失败模板没有配置", method.getName());
        } else {
            Map<String, String> templateMap = easyLogParser.process(easyLogOps, map, method, args, targetClass, executeResult.getErrMsg(), result);
            sendLog(easyLogOps, result, executeResult, templateMap);
        }
        //抛出异常
        if (!executeResult.isSuccess()){
            throw executeResult.getThrowable();
        }
        return result;
    }

    private void sendLog(EasyLogOps easyLogOps, Object result, MethodExecuteResult executeResult, Map<String, String> templateMap) {
        EasyLogInfo easyLogInfo = createEasyLogInfo(templateMap, easyLogOps);
        if (Objects.nonNull(easyLogInfo)) {
            easyLogInfo.setContent(executeResult.isSuccess() ? templateMap.get(easyLogOps.getContent()) : templateMap.get(easyLogOps.getFail()));
            easyLogInfo.setSuccess(executeResult.isSuccess());
            easyLogInfo.setResult(JsonUtils.toJSONString(result));
            easyLogInfo.setErrorMsg(executeResult.getErrMsg());
            easyLogInfo.setExecuteTime(executeResult.getExecuteTime());
            easyLogInfo.setOperateTime(executeResult.getOperateTime());
            logRecordService.record(easyLogInfo);
        }
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
        easyLogInfo.setType(easyLogOps.getType());
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
        easyLogOps.setType(easyLog.type());
        easyLogOps.setOperator(easyLog.operator());
        easyLogOps.setBizNo(easyLog.bizNo());
        easyLogOps.setTenant(easyLog.tenant());
        easyLogOps.setDetails(easyLog.detail());
        easyLogOps.setCondition(easyLog.condition());
        return easyLogOps;
    }

}

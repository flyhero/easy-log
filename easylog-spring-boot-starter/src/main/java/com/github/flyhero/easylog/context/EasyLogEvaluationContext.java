package com.github.flyhero.easylog.context;

import com.github.flyhero.easylog.constants.VarConsts;
import com.github.flyhero.easylog.util.JsonUtils;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;

/**
 * MethodBasedEvaluationContext 基于方法的上下文，主要作用：将方法参数放入到上下文中
 *
 * @author WangQingFei(qfwang666 @ 163.com)
 * @date 2022/2/26 14:46
 */
public class EasyLogEvaluationContext extends MethodBasedEvaluationContext {

    public EasyLogEvaluationContext(Method method, Object[] arguments, ParameterNameDiscoverer parameterNameDiscoverer) {
        super(null, method, arguments, parameterNameDiscoverer);
        super.lazyLoadArguments();
    }

    /**
     * 将方法执行结果放入上下文中
     *
     * @param errMsg 错误信息
     * @param result 返回结果
     */
    public void putResult(String errMsg, Object result) {
        super.setVariable(VarConsts.ERR_MSG, errMsg);
        super.setVariable(VarConsts.RESULT, JsonUtils.toJSONString(result));
    }
}

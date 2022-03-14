package com.github.flyhero.easylog.context;

import com.github.flyhero.easylog.constants.EasyLogConsts;
import com.github.flyhero.easylog.util.JsonUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
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

    public EasyLogEvaluationContext(Method method, Object[] arguments, ParameterNameDiscoverer parameterNameDiscoverer, BeanFactory beanFactory) {
        super(null, method, arguments, parameterNameDiscoverer);
        // setBeanResolver 主要用于支持SpEL模板中调用指定类的方法，如：@XXService.x(#root)
        super.setBeanResolver(new BeanFactoryResolver(beanFactory));
        super.lazyLoadArguments();
    }

    /**
     * 将方法执行结果放入上下文中
     *
     * @param errMsg 错误信息
     * @param result 返回结果
     */
    public void putResult(String errMsg, Object result) {
        super.setVariable(EasyLogConsts.ERR_MSG, errMsg);
        super.setVariable(EasyLogConsts.RESULT, JsonUtils.toJSONString(result));
    }
}

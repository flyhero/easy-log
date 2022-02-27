package com.github.flyhero.easylog.context;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;

/**
 *  MethodBasedEvaluationContext 基于方法的上下文，主要作用：将方法参数放入到上下文中
 * @author WangQingFei(qfwang666@163.com)
 * @date 2022/2/26 14:46
 */
public class EasyLogEvaluationContext extends MethodBasedEvaluationContext {

    public EasyLogEvaluationContext(Object rootObject, Method method, Object[] arguments, ParameterNameDiscoverer parameterNameDiscoverer) {
        super(rootObject, method, arguments, parameterNameDiscoverer);
        super.lazyLoadArguments();
    }
}

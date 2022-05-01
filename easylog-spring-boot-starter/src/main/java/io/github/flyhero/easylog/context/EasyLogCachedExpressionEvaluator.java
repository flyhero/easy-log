package io.github.flyhero.easylog.context;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存表达式求值器
 *
 * 参考 {@link org.springframework.cache.interceptor.CacheOperationExpressionEvaluator}
 * @author WangQingFei(qfwang666 @ 163.com)
 * @date 2022/5/1 15:32
 */
public class EasyLogCachedExpressionEvaluator extends CachedExpressionEvaluator {

    private final Map<ExpressionKey, Expression> keyCache = new ConcurrentHashMap<>(64);

    public EasyLogCachedExpressionEvaluator() {
    }

    public EvaluationContext createEvaluationContext(Method method, Object[] args, BeanFactory beanFactory, String errMsg, Object result) {
        EasyLogEvaluationContext evaluationContext = new EasyLogEvaluationContext(method, args, this.getParameterNameDiscoverer());
        evaluationContext.putResult(errMsg, result);
        if (beanFactory != null) {
            // setBeanResolver 主要用于支持SpEL模板中调用指定类的方法，如：@XXService.x(#root)
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }

        return evaluationContext;
    }

    public Object parseExpression(String expression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return this.getExpression(this.keyCache, methodKey, expression).getValue(evalContext);
    }

    void clear() {
        this.keyCache.clear();
    }
}

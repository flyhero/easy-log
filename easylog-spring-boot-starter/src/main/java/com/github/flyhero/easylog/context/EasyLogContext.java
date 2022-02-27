package com.github.flyhero.easylog.context;


import org.springframework.core.NamedThreadLocal;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class EasyLogContext {

    private static final ThreadLocal<StandardEvaluationContext> THREAD_LOCAL_CONTEXT = new NamedThreadLocal<>("ThreadLocal StandardEvaluationContext");

    public static StandardEvaluationContext getContext() {
        return THREAD_LOCAL_CONTEXT.get() == null ? new StandardEvaluationContext(): THREAD_LOCAL_CONTEXT.get();
    }

    public static void putVariable(String key, Object value) {
        StandardEvaluationContext context = THREAD_LOCAL_CONTEXT.get() == null ? new StandardEvaluationContext(): THREAD_LOCAL_CONTEXT.get();
        context.setVariable(key, value);
        THREAD_LOCAL_CONTEXT.set(context);
    }

    public static void clearContext() {
        THREAD_LOCAL_CONTEXT.remove();
    }


}

package io.github.flyhero.easylog.service;

/**
 * 获取操作者和租户
 * 可根据需要将操作者/租户信息实体转为 json字符串
 */
public interface IOperatorService {

    /**
     * 获取当前操作者
     *
     * @return
     */
    String getOperator();

    /**
     * 当前租户
     *
     * @return
     */
    String getTenant();
}

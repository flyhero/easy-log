package io.github.flyhero.easylog.model;

import io.github.flyhero.easylog.compare.FieldInfo;

import java.util.List;

public class EasyLogInfo {

    /**
     * 平台
     */
    private String platform;

    /**
     * 租户
     */
    private String tenant;

    /**
     * 操作者
     */
    private String operator;

    /**
     * 业务id
     */
    private String bizNo;

    /**
     * 模块
     */
    private String module;

    /**
     * 操作类型
     */
    private String type;

    /**
     * 成功操作内容
     */
    private String content;

    /**
     * 操作时间 时间戳单位：ms
     */
    private Long operateTime;

    /**
     * 操作花费的时间 单位：ms
     */
    private Long executeTime;

    /**
     * 是否调用成功
     */
    private Boolean success;

    /**
     * 执行后返回的json字符串
     */
    private String result;

    private String errorMsg;

    /**
     * 详细
     */
    private String details;

    /**
     * 详细的字段变更
     */
    private List<FieldInfo> compareFields;


    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Long executeTime) {
        this.executeTime = executeTime;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<FieldInfo> getCompareFields() {
        return compareFields;
    }

    public void setCompareFields(List<FieldInfo> compareFields) {
        this.compareFields = compareFields;
    }
}

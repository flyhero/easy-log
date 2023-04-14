package io.github.flyhero.easylog.model;

/**
 * 方法的执行结果
 *
 * @author WangQingFei(qfwang666 @ 163.com)
 * @date 2022/3/1 10:42
 */
public class MethodExecuteResult {

    private boolean success;

    private Throwable throwable;

    private String errMsg;

    private Long operateTime;

    private Long executeTime;

    public MethodExecuteResult(boolean success) {
        this.success = success;
        this.operateTime = System.currentTimeMillis();
    }

    public MethodExecuteResult(boolean success, Throwable throwable, String errMsg) {
        this.success = success;
        this.throwable = throwable;
        this.errMsg = errMsg;
    }

    public void calcExecuteTime() {
        this.executeTime = System.currentTimeMillis() - this.operateTime;
    }

    public void exception(Throwable throwable) {
        this.success = false;
        this.executeTime = System.currentTimeMillis() - this.operateTime;
        this.throwable = throwable;
        this.errMsg = throwable.getMessage();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
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
}

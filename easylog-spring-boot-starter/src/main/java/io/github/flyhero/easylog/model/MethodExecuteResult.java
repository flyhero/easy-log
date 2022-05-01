package io.github.flyhero.easylog.model;

import lombok.Data;

/**
 * 方法的执行结果
 *
 * @author WangQingFei(qfwang666 @ 163.com)
 * @date 2022/3/1 10:42
 */
@Data
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

}

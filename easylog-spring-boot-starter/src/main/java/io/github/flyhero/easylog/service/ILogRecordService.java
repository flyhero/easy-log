package io.github.flyhero.easylog.service;

import io.github.flyhero.easylog.model.EasyLogInfo;

public interface ILogRecordService {
    /**
     * 保存 log
     *
     * @param easyLogInfo 日志实体
     */
    void record(EasyLogInfo easyLogInfo);

}
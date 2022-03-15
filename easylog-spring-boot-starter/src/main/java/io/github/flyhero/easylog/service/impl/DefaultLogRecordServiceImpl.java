package io.github.flyhero.easylog.service.impl;

import io.github.flyhero.easylog.model.EasyLogInfo;
import io.github.flyhero.easylog.service.ILogRecordService;
import io.github.flyhero.easylog.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultLogRecordServiceImpl implements ILogRecordService {

    @Override
    public void record(EasyLogInfo easyLogInfo) {
        log.info("【logRecord】log={}", JsonUtils.toJSONString(easyLogInfo));
    }
}
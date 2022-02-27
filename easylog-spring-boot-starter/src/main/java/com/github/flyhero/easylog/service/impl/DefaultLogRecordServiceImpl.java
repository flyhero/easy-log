package com.github.flyhero.easylog.service.impl;

import com.github.flyhero.easylog.model.EasyLogInfo;
import com.github.flyhero.easylog.service.ILogRecordService;
import com.github.flyhero.easylog.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultLogRecordServiceImpl implements ILogRecordService {

    @Override
    public void record(EasyLogInfo easyLogInfo) {
        log.info("【logRecord】log={}", JsonUtils.toJSONString(easyLogInfo));
    }
}
package io.github.flyhero.easylog.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.flyhero.easylog.model.EasyLogInfo;
import io.github.flyhero.easylog.service.ILogRecordService;
import io.github.flyhero.easylog.util.JsonUtils;

public class DefaultLogRecordServiceImpl implements ILogRecordService {

    public static Logger log = LoggerFactory.getLogger(DefaultLogRecordServiceImpl.class);

    @Override
    public void record(EasyLogInfo easyLogInfo) {
        log.info("【logRecord】log={}", JsonUtils.toJSONString(easyLogInfo));
    }
}
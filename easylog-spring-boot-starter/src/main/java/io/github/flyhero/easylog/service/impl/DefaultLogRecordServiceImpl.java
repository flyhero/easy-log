package io.github.flyhero.easylog.service.impl;

import io.github.flyhero.easylog.model.EasyLogInfo;
import io.github.flyhero.easylog.service.ILogRecordService;
import io.github.flyhero.easylog.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLogRecordServiceImpl implements ILogRecordService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultLogRecordServiceImpl.class);

    @Override
    public void record(EasyLogInfo easyLogInfo) {
        logger.info("【logRecord】log={}", JsonUtils.toJSONString(easyLogInfo));
    }
}
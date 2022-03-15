package io.github.flyhero.easylog.example.custom.service;

import io.github.flyhero.easylog.model.EasyLogInfo;
import io.github.flyhero.easylog.service.ILogRecordService;
import io.github.flyhero.easylog.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 接收操作日志，可根据情况存储到数据库或发送到MQ
 *
 * @author WangQingFei(qfwang666 @ 163.com)
 * @date 2022/2/28 18:38
 */
@Slf4j
@Service
public class OpLogRecordService implements ILogRecordService {
    @Override
    public void record(EasyLogInfo easyLogInfo) {
        log.info("hello easy-log:{}", JsonUtils.toJSONString(easyLogInfo));
    }
}

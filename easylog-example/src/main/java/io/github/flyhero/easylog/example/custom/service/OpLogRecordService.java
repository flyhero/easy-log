package io.github.flyhero.easylog.example.custom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.github.flyhero.easylog.aop.EasyLogAspect;
import io.github.flyhero.easylog.model.EasyLogInfo;
import io.github.flyhero.easylog.service.ILogRecordService;
import io.github.flyhero.easylog.util.JsonUtils;

/**
 * 接收操作日志，可根据情况存储到数据库或发送到MQ
 *
 * @author WangQingFei(qfwang666 @ 163.com)
 * @date 2022/2/28 18:38
 */
@Service
public class OpLogRecordService implements ILogRecordService {
	public static Logger log = LoggerFactory.getLogger(OpLogRecordService.class);

	@Override
	public void record(EasyLogInfo easyLogInfo) {
		log.info("hello easy-log:{}", JsonUtils.toJSONString(easyLogInfo));
	}
}

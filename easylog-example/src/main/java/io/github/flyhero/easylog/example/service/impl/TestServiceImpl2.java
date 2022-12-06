package io.github.flyhero.easylog.example.service.impl;

import io.github.flyhero.easylog.annotation.EasyLog;
import io.github.flyhero.easylog.context.ApplicationContextHolder;
import io.github.flyhero.easylog.example.constants.OperateType;
import io.github.flyhero.easylog.example.dto.UserDto;
import io.github.flyhero.easylog.example.entity.UserEntity;
import io.github.flyhero.easylog.example.service.ITestService;
import io.github.flyhero.easylog.example.service.ITestService2;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

/**
 * @author WangQingFei(qfwang666@163.com)
 * @date 2022/2/27 22:20
 */
@Service
public class TestServiceImpl2 implements ITestService2 {

    @EasyLog(module = "用户管理", type = OperateType.SELECT, success = "'查询了用户信息：'+ #name")
    @Override
    public void test(String name) {
    }
}

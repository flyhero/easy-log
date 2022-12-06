package io.github.flyhero.easylog.example.service.impl;

import io.github.flyhero.easylog.annotation.EasyLog;
import io.github.flyhero.easylog.context.ApplicationContextHolder;
import io.github.flyhero.easylog.example.constants.OperateType;
import io.github.flyhero.easylog.example.dto.UserDto;
import io.github.flyhero.easylog.example.entity.UserEntity;
import io.github.flyhero.easylog.example.service.ITestService;
import io.github.flyhero.easylog.example.service.ITestService2;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author WangQingFei(qfwang666@163.com)
 * @date 2022/2/27 22:20
 */
@Service
public class TestServiceImpl implements ITestService {

    @Autowired
    private ITestService2 testService2;

    @EasyLog(module = "用户管理", type = OperateType.UPDATE, success = "更新了用户信息：{{#userDto.name}}")
    @Override
    public UserEntity update(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userDto.getId());
        userEntity.setName(userDto.getName());
        testService2.test(userDto.getName());
        return userEntity;
    }

    /**
     * 内部方法调用，启动类添加注解 @EnableAspectJAutoProxy(exposeProxy = true)
     * @param userDto
     */
    @EasyLog(module = "用户管理", type = OperateType.UPDATE, success = "内部调用用户信息：{{#userDto.name}}")
    @Override
    public void internalMethod(UserDto userDto) {
        //        ITestService bean = ApplicationContextHolder.getBean(ITestService.class);
        TestServiceImpl testService = (TestServiceImpl) AopContext.currentProxy();
        testService.update(userDto);
    }
}

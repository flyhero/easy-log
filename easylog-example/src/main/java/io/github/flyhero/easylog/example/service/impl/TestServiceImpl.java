package io.github.flyhero.easylog.example.service.impl;

import io.github.flyhero.easylog.annotation.EasyLog;
import io.github.flyhero.easylog.example.constants.OperateType;
import io.github.flyhero.easylog.example.dto.UserDto;
import io.github.flyhero.easylog.example.entity.UserEntity;
import io.github.flyhero.easylog.example.service.ITestService;
import org.springframework.stereotype.Service;

/**
 * @author WangQingFei(qfwang666@163.com)
 * @date 2022/2/27 22:20
 */
@Service
public class TestServiceImpl implements ITestService {

    @EasyLog(module = "用户管理", type = OperateType.UPDATE, success = "更新了用户信息：{{#userDto.name}}")
    @Override
    public UserEntity update(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userDto.getId());
        userEntity.setName(userDto.getName());
        return userEntity;
    }
}

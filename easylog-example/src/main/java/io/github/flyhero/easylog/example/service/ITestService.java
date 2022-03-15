package io.github.flyhero.easylog.example.service;

import io.github.flyhero.easylog.example.dto.UserDto;
import io.github.flyhero.easylog.example.entity.UserEntity;

/**
 * @author WangQingFei(qfwang666@163.com)
 * @date 2022/2/27 22:19
 */
public interface ITestService {

    /**
     * 更新用户信息
     * @param userDto
     * @return
     */
    UserEntity update(UserDto userDto);
}

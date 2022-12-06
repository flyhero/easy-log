package io.github.flyhero.easylog.example.controller;

import io.github.flyhero.easylog.annotation.EasyLog;
import io.github.flyhero.easylog.annotation.EasyLogs;
import io.github.flyhero.easylog.example.constants.OperateType;
import io.github.flyhero.easylog.example.dto.UserDto;
import io.github.flyhero.easylog.example.entity.UserEntity;
import io.github.flyhero.easylog.example.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author WangQingFei(qfwang666 @ 163.com)
 * @date 2022/2/26 11:40
 */
@RestController
public class TestController {

    @Autowired
    private ITestService testService;

    @EasyLog(module = "测试", type = OperateType.SELECT, success = "测试 {{#name}}")
    @GetMapping("/test")
    public String test(@RequestParam String name) {
        return name;
    }

    @EasyLog(module = "测试1", type = OperateType.SELECT, success = "测试 {getBeforeRealNameByName{#name}}")
    @GetMapping("/test1")
    public String test1(@RequestParam String name) {
        return name;
    }

    @EasyLog(module = "测试2", type = OperateType.SELECT, success = "测试 {getBeforeRealNameByName{#name}}",
            condition = "{{#name == 'easylog'}}")
    @GetMapping("/test2")
    public String test2(@RequestParam String name) {
        return name;
    }

    @EasyLog(module = "测试3", operator = "{{#userDto.toString()}}", type = OperateType.ADD,
            success = "测试 {getAfterRealNameByName{#userDto.name}}",
            condition = "{{#userDto.name == 'easylog-new'}}")
    @PostMapping("/test3")
    public String test3(@RequestBody UserDto userDto) {
        userDto.setName(userDto.getName() + "-new");
        return userDto.getName();
    }


    @EasyLog(module = "测试4", type = OperateType.SELECT, success = "'测试'+ #name", fail = "新增失败：{{#_errMsg}}")
    @GetMapping("/test4")
    public String test4(@RequestParam String name) {
        if ("easylog".equalsIgnoreCase(name)) {
            throw new RuntimeException("测试异常");
        }
        return name;
    }

    @EasyLog(module = "测试5", type = OperateType.SELECT, success = "查询结果： {{#_result}}", detail = "#name")
    @GetMapping("/test5")
    public String test5(@RequestParam String name) {
        return name;
    }

    /**
     * 测试 service 方法
     *
     * @param userDto
     * @return
     */
    @PostMapping("/test6")
    public String test6(@RequestBody UserDto userDto) {
        UserEntity user = testService.update(userDto);
        return user.getName();
    }

    @PostMapping("/test7")
    public String test7(@RequestBody UserDto userDto) {
        testService.internalMethod(userDto);
        return "";
    }

    @GetMapping("/test8")
    @EasyLogs({
            @EasyLog(module = "测试8", type = OperateType.UPDATE, success = "测试多个日志-1： {getBeforeRealNameByName{#name}}"),
            @EasyLog(module = "测试8", type = OperateType.SELECT, success = "测试多个日志-2： {getBeforeRealNameByName{#name}}")
    })
    public String test8(String name){
        return name;
    }

    @GetMapping("/test9")
    public String test9(String name){
        testService.manyLog(name);
        return name;
    }
}

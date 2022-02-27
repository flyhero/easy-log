package com.github.flyhero.easylog.example.controller;

import com.github.flyhero.easylog.annotation.EasyLog;
import com.github.flyhero.easylog.example.dto.UserDto;
import com.github.flyhero.easylog.example.entity.UserEntity;
import com.github.flyhero.easylog.example.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author WangQingFei(qfwang666@163.com)
 * @date 2022/2/26 11:40
 */
@RestController
public class TestController {

    @Autowired
    private ITestService testService;

    @EasyLog(module = "测试", operateType = "查询", content = "测试 {{#name}}")
    @GetMapping("/test")
    public String test(@RequestParam String name) {
        return name;
    }

    @EasyLog(module = "测试1", operateType = "查询", content = "测试 {getBeforeRealNameByName{#name}}")
    @GetMapping("/test1")
    public String test1(@RequestParam String name) {
        return name;
    }

    @EasyLog(module = "测试2", operateType = "查询", content = "测试 {getBeforeRealNameByName{#name}}",
            condition = "{{#name == 'easylog'}}")
    @GetMapping("/test2")
    public String test2(@RequestParam String name) {
        return name;
    }

    @EasyLog(module = "测试3", operator = "{{#userDto.toString()}}", operateType = "新增",
            content = "测试 {getAfterRealNameByName{#userDto.name}}",
            condition = "{{#userDto.name == 'easylog-new'}}")
    @PostMapping("/test3")
    public String test3(@RequestBody UserDto userDto) {
        userDto.setName(userDto.getName()+"-new");
        return userDto.getName();
    }


    @EasyLog(module = "测试4", operateType = "查询", content = "测试 {{#name}}", fail = "新增失败：{{#errMsg}}")
    @GetMapping("/test4")
    public String test4(@RequestParam String name) {
        if ("easylog".equalsIgnoreCase(name)){
            throw new RuntimeException("测试异常");
        }
        return name;
    }

    @EasyLog(module = "测试5", operateType = "查询", content = "查询结果： {{#result}}")
    @GetMapping("/test5")
    public String test5(@RequestParam String name) {
        return name;
    }

    /**
     * 测试 service 方法
     * @param userDto
     * @return
     */
    @PostMapping("/test6")
    public String test6(@RequestBody UserDto userDto) {
        UserEntity user = testService.update(userDto);
        return user.getName();
    }
}

## 1. 项目简介
easy-log是基于SpringBoot的一款通用操作日志组件，它指在帮助我们通过注解优雅地聚合项目中的操作日志，对业务代码无侵入。

## 2. 使用场景
一般系统都会有操作日志，通过操作日志可追溯到 某人在某时干了某事情，如：

| 租户  | 操作人 | 时间               | 操作 | 内容                           |
| ----- | ------ | ------------------ | ---- | ------------------------------ |
| A租户 | 小明   | 2022/2/27 20:15:00 | 新增 | 新增了一个用户：Mr.Wang        |
| B租户 | 大米   | 2022/2/28 10:35:00 | 更新 | 修改订单 [xxxxxx] 价格为 xx 元 |
| C租户 | 老王   | 2022/2/28 22:55:00 | 查询 | 查询了名为: [xx] 的所有交易    |


## 3. 功能特性
 - 快速接入：基于SpringBoot，轻量级，引入starter即可食用
 - SpEL解析：直接写表达式解析入参
 - 自定义函数：支持目标方法执行前/后的自定义函数

## 4. 使用方法
### 4.1 引入依赖
在pom.xml中引入：
```
<dependency>
    <groupId>com.github.flyhero</groupId>
    <artifactId>easylog-spring-boot-starter</artifactId>
    <version>{版本号}</version>
</dependency>
```
### 4.2 使用注解
在要记录操作日志的方法上添加EasyLog注解并填写对应内容：

```java
@EasyLog(module = "用户模块", operator = "{{#userDto.toString()}}", operateType = "新增",
        content = "测试 {functionName{#userDto.name}}",
        condition = "{{#userDto.name == 'easylog'}}")
public String test(UserDto userDto) {
    return "test";
}
```
| 字段        | 意义                            | 支持表达式 | 必填 |
| ----------- | ------------------------------- | ---------- | ---- |
| tenant      | 租户，SAAS系统中区分不同租户    | 是         | 否   |
| operator    | 操作者                          | 是         | 否   |
| module      | 模块，区分不同业务模块          | 否         | 否   |
| operateType | 操作类型，形如：增删改查        | 否         | 否   |
| bizNo       | 业务编号，操作关联的主题标志    | 是         | 否   |
| content     | 日志模板内容                    | 是         | 是   |
| fail        | 操作失败时的模板内容            | 是         | 否   |
| detail      | 详细的记录信息                  | 是         | 否   |
| condition   | 是否记录的条件 (默认:true 记录) | 是         | 否   |



### 4.3 获取操作者
如果不在上述注解中指定租户和操作人，那么可统一设置，方式如下：
实现 **IOperatorService** 接口，并交给Spring管理。
```java
@Service
public class OperatorGetService implements IOperatorService {
    @Override
    public String getOperator() {
        return "admin";
    }
    @Override
    public String getTenant() {
        return "company";
    }
}
```

### 4.4 自定义函数

实现 **ICustomFunction** 接口，并交给Spring管理。
```java
@Component
public class GetRealNameById implements ICustomFunction {
    @Override
    public boolean executeBefore() {
        return false;
    }
    @Override
    public String functionName() {
        return "functionName";
    }
    @Override
    public String apply(String value) {
        return "easylog".equals(value) ? "good" : value;
    }
}
```
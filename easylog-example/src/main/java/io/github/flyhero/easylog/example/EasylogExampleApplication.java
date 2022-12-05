package io.github.flyhero.easylog.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
// 此注解在 同类中内部方法调用时添加， 项目README中有说明
//@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class EasylogExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasylogExampleApplication.class, args);
    }

}

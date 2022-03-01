package com.github.flyhero.easylog.configuration;

import com.github.flyhero.easylog.annotation.EasyLog;
import com.github.flyhero.easylog.function.CustomFunctionFactory;
import com.github.flyhero.easylog.function.EasyLogParser;
import com.github.flyhero.easylog.function.impl.DefaultCustomFunction;
import com.github.flyhero.easylog.function.ICustomFunction;
import com.github.flyhero.easylog.function.IFunctionService;
import com.github.flyhero.easylog.service.ILogRecordService;
import com.github.flyhero.easylog.service.IOperatorService;
import com.github.flyhero.easylog.function.impl.DefaultFunctionServiceImpl;
import com.github.flyhero.easylog.service.impl.DefaultLogRecordServiceImpl;
import com.github.flyhero.easylog.service.impl.DefaultOperatorServiceImpl;
import com.github.flyhero.easylog.util.EasyLogVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@ComponentScan("com.github.flyhero.easylog")
public class EasyLogAutoConfiguration {

    @PostConstruct
    public void printBanner(){
        System.out.println("                        _             \n" +
                        "                       | |            \n" +
                        "  ___  __ _ ___ _   _  | | ___   __ _ \n" +
                        " / _ \\/ _` / __| | | | | |/ _ \\ / _` |\n" +
                        "|  __/ (_| \\__ \\ |_| | | | (_) | (_| |\n" +
                        " \\___|\\__,_|___/\\__, | |_|\\___/ \\__, |\n" +
                        "                 __/ |           __/ |\n" +
                        "                |___/           |___/ \n");
        System.out.println("                        " + EasyLogVersion.getVersion() + " ");
    }

    @Bean
    @ConditionalOnMissingBean(ICustomFunction.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public ICustomFunction customFunction(){
        return new DefaultCustomFunction();
    }


    @Bean
    public CustomFunctionFactory CustomFunctionRegistrar(@Autowired List<ICustomFunction> iCustomFunctionList){
        return new CustomFunctionFactory(iCustomFunctionList);
    }

    @Bean
    public IFunctionService customFunctionService(CustomFunctionFactory customFunctionFactory){
        return new DefaultFunctionServiceImpl(customFunctionFactory);
    }

    @Bean
    public EasyLogParser easyLogParser(IFunctionService functionService){
        return new EasyLogParser(functionService);
    }

    @Bean
    @ConditionalOnMissingBean(IOperatorService.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public IOperatorService operatorGetService() {
        return new DefaultOperatorServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(ILogRecordService.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public ILogRecordService recordService() {
        return new DefaultLogRecordServiceImpl();
    }
}

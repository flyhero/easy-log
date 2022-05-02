package io.github.flyhero.easylog.configuration;

import io.github.flyhero.easylog.aop.EasyLogAspect;
import io.github.flyhero.easylog.function.CustomFunctionFactory;
import io.github.flyhero.easylog.function.EasyLogParser;
import io.github.flyhero.easylog.function.ICustomFunction;
import io.github.flyhero.easylog.function.IFunctionService;
import io.github.flyhero.easylog.function.impl.DefaultCustomFunction;
import io.github.flyhero.easylog.function.impl.DefaultFunctionServiceImpl;
import io.github.flyhero.easylog.service.ILogRecordService;
import io.github.flyhero.easylog.service.IOperatorService;
import io.github.flyhero.easylog.service.impl.DefaultLogRecordServiceImpl;
import io.github.flyhero.easylog.service.impl.DefaultOperatorServiceImpl;
import io.github.flyhero.easylog.util.EasyLogVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@ComponentScan("io.github.flyhero.easylog")
@ConditionalOnProperty(prefix = "easylog", name = "enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({EasyLogProperties.class})
public class EasyLogAutoConfiguration {

    @Autowired
    private EasyLogProperties easyLogProperties;

    @PostConstruct
    public void printBanner() {
        if (!easyLogProperties.isBanner()){
            return;
        }
        System.out.println("                        _             \n" +
                "                       | |            \n" +
                "  ___  __ _ ___ _   _  | | ___   __ _ \n" +
                " / _ \\/ _` / __| | | | | |/ _ \\ / _` |\n" +
                "|  __/ (_| \\__ \\ |_| | | | (_) | (_| |\n" +
                " \\___|\\__,_|___/\\__, | |_|\\___/ \\__, |\n" +
                "                 __/ |           __/ |\n" +
                "                |___/           |___/ \n");
        System.out.println("  <<easy-log>>            " + EasyLogVersion.getVersion() + " ");
    }

    @Bean
    @ConditionalOnMissingBean(ICustomFunction.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public ICustomFunction customFunction() {
        return new DefaultCustomFunction();
    }

    @Bean
    public CustomFunctionFactory CustomFunctionRegistrar(@Autowired List<ICustomFunction> iCustomFunctionList) {
        return new CustomFunctionFactory(iCustomFunctionList);
    }

    @Bean
    public IFunctionService customFunctionService(CustomFunctionFactory customFunctionFactory) {
        return new DefaultFunctionServiceImpl(customFunctionFactory);
    }

    @Bean
    public EasyLogParser easyLogParser() {
        return new EasyLogParser();
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

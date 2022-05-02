package io.github.flyhero.easylog.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * easylog 操作日志配置
 *
 * @author WangQingFei(qfwang666 @ 163.com)
 * @date 2022/5/1 22:33
 */
@Data
@ConfigurationProperties(prefix = "easylog")
public class EasyLogProperties {
    /**
     * 是否开启操作日志，默认开启
     */
    private boolean enable = true;

    /**
     * 平台：不同服务使用的区分，默认取 spring.application.name
     */
    @Value("${spring.application.name:#{null}}")
    private String platform;

    /**
     * 是否在控制台打印 banner，默认打印
     */
    private boolean banner = true;
}

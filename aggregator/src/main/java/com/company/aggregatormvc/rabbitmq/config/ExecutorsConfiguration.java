package com.company.aggregatormvc.rabbitmq.config;

import com.company.aggregatormvc.rabbitmq.property.ExecutorsProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

@Configuration
@Slf4j
@AllArgsConstructor
@EnableConfigurationProperties(ExecutorsProperties.class)
public class ExecutorsConfiguration {

    private final ExecutorsProperties executorsProperties;

    @Bean
    public ThreadPoolExecutorFactoryBean coreExecutor() {
        log.info("Main scheduler of execution of tasks initialization");
        ThreadPoolExecutorFactoryBean result = new ThreadPoolExecutorFactoryBean();
        result.setCorePoolSize(executorsProperties.getCorePoolSize());
        result.setMaxPoolSize(executorsProperties.getCorePoolSize() * 2);
        result.setThreadNamePrefix("aggregator-e-core-");
        return result;
    }

}

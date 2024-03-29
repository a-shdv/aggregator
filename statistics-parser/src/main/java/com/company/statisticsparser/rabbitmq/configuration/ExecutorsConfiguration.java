package com.company.statisticsparser.rabbitmq.configuration;

import com.company.statisticsparser.rabbitmq.property.ExecutorsProperties;
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
        log.info("Инициализация основного планировщика выполнения задач");
        ThreadPoolExecutorFactoryBean result = new ThreadPoolExecutorFactoryBean();
        result.setCorePoolSize(executorsProperties.getCorePoolSize());
        result.setMaxPoolSize(executorsProperties.getCorePoolSize() * 2);
        result.setThreadNamePrefix("parser-e-core-");
        return result;
    }

}

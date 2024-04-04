package com.company.vacanciesparser.rabbitmq.configurations;

import com.company.vacanciesparser.rabbitmq.properties.ExecutorsProperties;
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
        int corePoolSize = executorsProperties.getCorePoolSize() / 3;
        ThreadPoolExecutorFactoryBean result = new ThreadPoolExecutorFactoryBean();
        result.setCorePoolSize(corePoolSize);
        result.setMaxPoolSize(corePoolSize * 2);
        result.setThreadNamePrefix("aggregator-e-core-");
        return result;
    }   

}

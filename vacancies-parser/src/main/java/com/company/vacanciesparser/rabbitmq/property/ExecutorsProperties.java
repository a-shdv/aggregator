package com.company.vacanciesparser.rabbitmq.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "parser.executors")
@Data
public class ExecutorsProperties {

    /**
     * количество пула потоков для основного планировщика
     */
    private Integer corePoolSize = Runtime.getRuntime().availableProcessors();
}

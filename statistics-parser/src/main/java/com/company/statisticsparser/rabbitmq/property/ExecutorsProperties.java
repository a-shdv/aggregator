package com.company.statisticsparser.rabbitmq.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "statistics.executors")
@Data
public class ExecutorsProperties {

    /**
     * количество пула потоков для основного планировщика
     */
    private Integer corePoolSize = Runtime.getRuntime().availableProcessors() / Runtime.getRuntime().availableProcessors();
}

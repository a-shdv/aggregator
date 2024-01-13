package com.company.hhparser.rabbitmq.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hh.executors")
@Data
public class ExecutorsProperties {

    /**
     * количество пула потоков для основного планировщика
     */
    private Integer corePoolSize = Runtime.getRuntime().availableProcessors();
}

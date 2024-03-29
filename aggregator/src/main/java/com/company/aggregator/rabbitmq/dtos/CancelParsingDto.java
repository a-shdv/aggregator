package com.company.aggregator.rabbitmq.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelParsingDto  implements Serializable {
    Boolean isParsingCancelled;
}

package com.company.aggregator.rabbitmq.dtos.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageDto implements Serializable {
    String profession;
    String city;
    String year;
    String currency;
}
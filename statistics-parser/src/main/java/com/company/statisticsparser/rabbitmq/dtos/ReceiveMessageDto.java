package com.company.statisticsparser.rabbitmq.dtos;

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
public class ReceiveMessageDto implements Serializable {
    String profession;
    String city;
    String year;
    Integer currency;
}

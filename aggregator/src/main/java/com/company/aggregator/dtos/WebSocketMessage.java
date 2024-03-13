package com.company.aggregator.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketMessage {
    String username;
    String title;
    BigDecimal salary;
    Boolean onlyWithSalary;
    Integer experience;
    Integer cityId;
    Boolean isRemoteAvailable;
}

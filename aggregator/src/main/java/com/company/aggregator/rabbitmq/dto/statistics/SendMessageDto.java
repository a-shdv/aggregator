package com.company.aggregator.rabbitmq.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageDto implements Serializable {
    String username;
    String profession;
    String city;
    String year;
    String currency;
}
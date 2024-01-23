package com.company.aggregator.rabbitmq.dto;

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
    String title;
    Integer amount;

    @Override
    public String toString() {
        return "ReceiveMessageDto {" +
                "title='" + title + '\'' +
                "amount='" + amount + '\'' +
                '}';
    }
}
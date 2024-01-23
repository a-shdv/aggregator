package com.company.aggregator.rabbitmq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
public class SendMessageDto implements Serializable {
    String title;
    Integer amount;

    public SendMessageDto(String title, Integer amount) {
        this.title = title;
        this.amount = amount / 2;
    }

    @Override
    public String toString() {
        return "ReceiveMessageDto {" +
                "title='" + title + '\'' +
                "amount='" + amount + '\'' +
                '}';
    }
}
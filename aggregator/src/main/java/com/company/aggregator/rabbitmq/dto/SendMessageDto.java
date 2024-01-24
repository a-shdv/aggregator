package com.company.aggregator.rabbitmq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Builder
public class SendMessageDto implements Serializable {
    String title;
    Integer amount;
    BigDecimal salary;

    public SendMessageDto(String title, Integer amount, BigDecimal salary) {
        this.title = title;
        this.amount = amount / 2;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "ReceiveMessageDto {" +
                "title='" + title + '\'' +
                "amount='" + amount + '\'' +
                "salary='" + salary + '\'' +
                '}';
    }
}
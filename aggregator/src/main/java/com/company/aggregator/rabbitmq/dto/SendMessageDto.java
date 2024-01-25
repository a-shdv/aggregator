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
    int amount;
    BigDecimal salary;
    boolean onlyWithSalary;
    int experience;

    public SendMessageDto(String title, int amount, BigDecimal salary, boolean onlyWithSalary, int experience) {
        this.title = title;
        this.amount = amount / 2;
        this.salary = salary;
        this.onlyWithSalary = onlyWithSalary;
        this.experience = experience;
    }

    @Override
    public String toString() {
        return "ReceiveMessageDto {" +
                "title='" + title + '\'' +
                "amount='" + amount + '\'' +
                "salary='" + salary + '\'' +
                "onlyWithSalary='" + onlyWithSalary + '\'' +
                "experience='" + experience + '\'' +
                '}';
    }
}
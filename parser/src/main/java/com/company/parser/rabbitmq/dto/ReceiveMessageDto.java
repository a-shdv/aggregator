package com.company.parser.rabbitmq.dto;

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
    String title;
    int amount;
    BigDecimal salary;
    boolean onlyWithSalary;
    int experience;

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

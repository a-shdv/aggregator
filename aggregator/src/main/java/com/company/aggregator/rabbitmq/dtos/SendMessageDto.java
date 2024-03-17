package com.company.aggregator.rabbitmq.dtos;

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
    String username;
    String title;
    //    int amount;
    BigDecimal salary;
    Boolean onlyWithSalary;
    Integer experience;
    Integer cityId;
    Boolean isRemoteAvailable;

    @Override
    public String toString() {
        return "SendMessageDto {" +
                "title='" + title + '\'' +
//                "amount='" + amount + '\'' +
                "salary='" + salary + '\'' +
                "onlyWithSalary='" + onlyWithSalary + '\'' +
                "experience='" + experience + '\'' +
                "cityId='" + cityId + '\'' +
                "isRemoteAvailable='" + isRemoteAvailable + '\'' +
                '}';
    }
}
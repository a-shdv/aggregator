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
    boolean onlyWithSalary;
    int experience;
    int cityId;
    boolean isRemoteAvailable;

    @Override
    public String toString() {
        return "ReceiveMessageDto {" +
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
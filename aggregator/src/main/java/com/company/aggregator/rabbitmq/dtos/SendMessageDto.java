package com.company.aggregator.rabbitmq.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Builder
public class SendMessageDto implements Serializable {
    String username;
    String title;
    int amount;
    BigDecimal salary;
    boolean onlyWithSalary;
    int experience;
    int cityId;
    boolean isRemoteAvailable;

    public SendMessageDto(String username, String title, int amount, BigDecimal salary, boolean onlyWithSalary, int experience, int cityId, boolean isRemoteAvailable) {
        this.username = username;
        this.title = title;
        this.amount = amount / 3;
        this.salary = salary;
        this.onlyWithSalary = onlyWithSalary;
        this.experience = experience;
        this.cityId = cityId;
        this.isRemoteAvailable = isRemoteAvailable;
    }

    @Override
    public String toString() {
        return "ReceiveMessageDto {" +
                "title='" + title + '\'' +
                "amount='" + amount + '\'' +
                "salary='" + salary + '\'' +
                "onlyWithSalary='" + onlyWithSalary + '\'' +
                "experience='" + experience + '\'' +
                "cityId='" + cityId + '\'' +
                "isRemoteAvailable='" + isRemoteAvailable + '\'' +
                '}';
    }
}
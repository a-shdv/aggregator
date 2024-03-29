package com.company.vacanciesparser.rabbitmq.dtos;

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
    String username;
    String title;
    //    int amount;
    BigDecimal salary;
    Boolean isOnlyWithSalary;
    Integer experience;
    Integer cityId;
    Boolean isRemoteAvailable;
//    Boolean isParsingCancelled;

    @Override
    public String toString() {
        return "ReceiveMessageDto {" +
                "username='" + username + '\'' +
                "title='" + title + '\'' +
//                "amount='" + amount + '\'' +
                "salary='" + salary + '\'' +
                "onlyWithSalary='" + isOnlyWithSalary + '\'' +
                "experience='" + experience + '\'' +
                "cityId='" + cityId + '\'' +
                "isRemoteAvailable='" + isRemoteAvailable + '\'' +
                '}';
    }
}

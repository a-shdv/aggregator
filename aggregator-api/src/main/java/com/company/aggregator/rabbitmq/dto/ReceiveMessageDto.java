package com.company.aggregator.rabbitmq.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ReceiveMessageDto implements Serializable {
    String title;
    String date;
    String salary;
    String company;
    String requirements;
    String description;
}
package com.company.habrparser.rabbitmq.dto;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class SendMessageDto implements Serializable {
    String title;
    String date;
    String salary;
    String company;
    String requirements;
    String description;
    String schedule;
    String source;

    @Override
    public String toString() {
        return "\nSendMessageDto {" +
                "\n\ttitle='" + title + '\'' +
                ", \n\tdate='" + date + '\'' +
                ", \n\tsalary='" + salary + '\'' +
                ", \n\tcompany='" + company + '\'' +
                ", \n\trequirements='" + requirements + '\'' +
                ", \n\tdescription='" + description + '\'' +
                ", \n\tschedule='" + schedule + '\'' +
                ", \n\tsource='" + source + "\'" + "\n" +
                '}';
    }
}
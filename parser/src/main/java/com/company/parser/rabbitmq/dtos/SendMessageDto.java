package com.company.parser.rabbitmq.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageDto implements Serializable {
    String username;
    String title;
    String date;
    String salary;
    String company;
    String requirements;
    String description;
    String schedule;
    String source;
    String logo;

    @Override
    public String toString() {
        return "\nSendMessageDto {" +
                "\n\tusername='" + username + '\'' +
                "\n\ttitle='" + title + '\'' +
                ", \n\tdate='" + date + '\'' +
                ", \n\tsalary='" + salary + '\'' +
                ", \n\tcompany='" + company + '\'' +
                ", \n\trequirements='" + requirements + '\'' +
                ", \n\tdescription='" + description + '\'' +
                ", \n\tschedule='" + schedule + '\'' +
                ", \n\tsource='" + source + "\'" + "\n" +
                ", \n\tlogo='" + logo + "\'" + "\n" +
                '}';
    }
}
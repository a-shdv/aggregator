package com.company.parser.rabbitmq.dtos;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
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

    public SendMessageDto() {
    }

    public SendMessageDto(String username, String title, String date, String salary, String company, String requirements, String description, String schedule, String source) {
        this.username = username;
        this.title = title;
        this.date = date;
        this.salary = salary;
        this.company = company;
        this.requirements = requirements;
        this.description = description;
        this.schedule = schedule;
        this.source = source;
    }

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
                '}';
    }
}
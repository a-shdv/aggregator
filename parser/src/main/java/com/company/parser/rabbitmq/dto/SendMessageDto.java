package com.company.parser.rabbitmq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
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

    public SendMessageDto() {
    }

    public SendMessageDto(String title, String date, String salary, String company, String requirements, String description, String schedule, String source) {
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
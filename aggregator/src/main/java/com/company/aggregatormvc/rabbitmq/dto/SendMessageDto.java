package com.company.aggregatormvc.rabbitmq.dto;

import com.company.aggregatormvc.model.Vacancy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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

    public static Vacancy toVacancy(SendMessageDto receiveMessageDto) {
        return Vacancy.builder()
                .title(receiveMessageDto.getTitle())
                .date(receiveMessageDto.getDate())
                .salary(receiveMessageDto.getSalary())
                .company(receiveMessageDto.getCompany())
                .requirements(receiveMessageDto.getRequirements())
                .description(receiveMessageDto.getDescription())
                .schedule(receiveMessageDto.getSchedule())
                .source(receiveMessageDto.getSource())
                .build();
    }

    @Override
    public String toString() {
        return "\nReceiveMessageDto {" +
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
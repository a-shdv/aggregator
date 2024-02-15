package com.company.aggregator.rabbitmq.dtos;

import com.company.aggregator.models.Vacancy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiveMessageDto implements Serializable {
    String username;
    String title;
    String date;
    String salary;
    String company;
    String requirements;
    String description;
    String schedule;
    String source;

    public static Vacancy toVacancy(ReceiveMessageDto receiveMessageDto) {

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

    public static List<Vacancy> toVacancyList(List<ReceiveMessageDto> receiveMessageDtoList) {
        List<Vacancy> vacancies = new ArrayList<>();
        receiveMessageDtoList.forEach(message -> {
            vacancies.add(Vacancy.builder()
                    .title(message.getTitle())
                    .date(message.getDate())
                    .salary(message.getSalary())
                    .company(message.getCompany())
                    .requirements(message.getRequirements())
                    .description(message.getDescription())
                    .schedule(message.getSchedule())
                    .source(message.getSource())
                    .build());
        });
        return vacancies;
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
package com.company.aggregator.rabbitmq.dtos.statistics;

import com.company.aggregator.models.Statistics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiveMessageDto implements Serializable {
    String username;
    String avgSalaryTitle;
    String avgSalaryDescription;
    String medianSalaryTitle;
    String medianSalaryDescription;
    String modalSalaryTitle;
    String modalSalaryDescription;
    String profession;
    String city;
    String year;
    String currency;

    public static Statistics toStatistics(ReceiveMessageDto dto) {
        return Statistics.builder()
                .username(dto.getUsername())
                .avgSalaryTitle(dto.avgSalaryTitle)
                .avgSalaryDescription(dto.avgSalaryDescription)
                .medianSalaryTitle(dto.medianSalaryTitle)
                .medianSalaryDescription(dto.medianSalaryDescription)
                .modalSalaryTitle(dto.modalSalaryTitle)
                .modalSalaryDescription(dto.modalSalaryDescription)
                .profession(dto.profession)
                .city(dto.city)
                .year(dto.year)
                .currency(dto.currency)
                .build();
    }
}
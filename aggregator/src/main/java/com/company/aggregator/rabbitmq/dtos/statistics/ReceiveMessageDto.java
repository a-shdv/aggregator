package com.company.aggregator.rabbitmq.dtos.statistics;

import com.company.aggregator.dtos.StatisticsDto;
import com.company.aggregator.models.Statistics;
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
    String avgSalaryTitle;
    String avgSalaryDescription;
    String medianSalaryTitle;
    String medianSalaryDescription;
    String modalSalaryTitle;
    String modalSalaryDescription;

    public static Statistics toStatistics(ReceiveMessageDto dto) {
        return Statistics.builder()
                .username(dto.getUsername())
                .avgSalaryTitle(dto.avgSalaryTitle)
                .avgSalaryDescription(dto.avgSalaryDescription)
                .medianSalaryTitle(dto.medianSalaryTitle)
                .medianSalaryDescription(dto.medianSalaryDescription)
                .modalSalaryTitle(dto.modalSalaryTitle)
                .modalSalaryDescription(dto.modalSalaryDescription)
                .build();
    }
}
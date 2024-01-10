package com.company.habrparser.rabbitmq.dto;

import com.company.habrparser.model.Vacancy;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class SendMessageDto implements Serializable {
    private Vacancy vacancy;
}
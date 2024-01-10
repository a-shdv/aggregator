package com.company.habrparser.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Vacancy {
    String title;
    String date;
    String salary;
    String company;
    String requirements;
    String description;
}

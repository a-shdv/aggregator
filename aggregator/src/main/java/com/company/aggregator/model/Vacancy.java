package com.company.aggregator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "vacancies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String date;
    private String salary;
    private String company;
    @Column(columnDefinition = "text") private String requirements;
    @Column(columnDefinition = "text") private String description;
    @Column(columnDefinition = "text") private String schedule;
    @Column(columnDefinition = "text") private String source;

}

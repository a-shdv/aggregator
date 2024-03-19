package com.company.aggregator.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    String avgSalaryTitle;
    String avgSalaryDescription;
    String medianSalaryTitle;
    String medianSalaryDescription;
    String modalSalaryTitle;
    String modalSalaryDescription;
    String pictureDiagrams;
    String pictureCharts;
    @OneToOne(mappedBy = "statistics", cascade = CascadeType.ALL) private User user;
}

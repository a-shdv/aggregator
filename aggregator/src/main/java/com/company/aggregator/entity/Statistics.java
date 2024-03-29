package com.company.aggregator.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "statistics",
        indexes = {
                @Index(name = "idx_statistics_username", columnList = "username")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Statistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
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
    @OneToOne(mappedBy = "statistics")
    User user;
}

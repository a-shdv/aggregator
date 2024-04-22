package com.company.aggregator.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table
@Getter
@Setter
@ToString(exclude = "user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Statistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

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

    String username;

    @OneToOne
    @JoinColumn
    User user;
}

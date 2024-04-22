package com.company.aggregator.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;

    BigDecimal salary;

    Boolean onlyWithSalary;

    Integer experience;

    Integer cityId;

    Boolean isRemoteAvailable;

    Integer numOfRequests;

    @OneToOne
    @JoinColumn
    User user;
}

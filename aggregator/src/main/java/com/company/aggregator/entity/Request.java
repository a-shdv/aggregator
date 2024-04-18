package com.company.aggregator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private BigDecimal salary;
    private Boolean onlyWithSalary;
    private Integer experience;
    private Integer cityId;
    private Boolean isRemoteAvailable;
    private Integer numOfRequests;

    @OneToOne(mappedBy = "request")
    private User user;
}

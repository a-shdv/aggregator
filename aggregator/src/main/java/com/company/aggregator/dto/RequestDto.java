package com.company.aggregator.dto;

import com.company.aggregator.entity.Request;

import java.math.BigDecimal;

public record RequestDto(String username, String title, BigDecimal salary, Boolean onlyWithSalary,
                         Integer experience, Integer cityId, Boolean isRemoteAvailable, Integer numOfRequests) {
    public RequestDto(String username, String title, BigDecimal salary, Boolean onlyWithSalary, Integer experience, Integer cityId, Boolean isRemoteAvailable, Integer numOfRequests) {
        this.username = username;
        this.title = title;
        this.salary = salary;
        this.onlyWithSalary = onlyWithSalary;
        this.experience = experience;
        this.cityId = cityId;
        this.isRemoteAvailable = isRemoteAvailable;
        this.numOfRequests = numOfRequests;
    }

    public static Request toRequest(RequestDto requestDto) {
        return Request.builder()
                .title(requestDto.title())
                .salary(requestDto.salary())
                .onlyWithSalary(requestDto.onlyWithSalary())
                .experience(requestDto.experience())
                .cityId(requestDto.cityId())
                .isRemoteAvailable(requestDto.isRemoteAvailable())
                .numOfRequests(requestDto.numOfRequests())
                .build();
    }
}

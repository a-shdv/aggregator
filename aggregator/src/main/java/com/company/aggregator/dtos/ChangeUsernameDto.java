package com.company.aggregator.dtos;

import lombok.Builder;
import lombok.Getter;

@Builder
public record ChangeUsernameDto(@Getter String username) {
    public ChangeUsernameDto(String username) {
        this.username = username;
    }
}

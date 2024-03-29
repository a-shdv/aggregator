package com.company.aggregator.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
public record ChangeUsernameDto(@Getter String username) {
    public ChangeUsernameDto(String username) {
        this.username = username;
    }
}

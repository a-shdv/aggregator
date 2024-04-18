package com.company.aggregator.dto;

import lombok.Getter;

public record UserLockStatusDto(@Getter Long id, @Getter String email, @Getter String username) {
    public UserLockStatusDto(Long id, String email, String username) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}

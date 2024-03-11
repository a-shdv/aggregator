package com.company.aggregator.dtos;

import lombok.Builder;
import lombok.Getter;

@Builder
public record ChangePasswordDto(@Getter String oldPassword, @Getter String newPassword,
                                @Getter String confirmNewPassword) {
    public ChangePasswordDto(String oldPassword, String newPassword, String confirmNewPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }
}

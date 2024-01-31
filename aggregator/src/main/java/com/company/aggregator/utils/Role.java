package com.company.aggregator.utils;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER(0),
    ADMIN(1);

    private int code;

    Role(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getAuthority() {
        return null;
    }
}

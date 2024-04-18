package com.company.aggregator.enumeration;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum Role implements GrantedAuthority {
    USER(0),
    ADMIN(1);

    private int code;

    Role(int code) {
        this.code = code;
    }

    @Override
    public String getAuthority() {
        return name();
    }
}

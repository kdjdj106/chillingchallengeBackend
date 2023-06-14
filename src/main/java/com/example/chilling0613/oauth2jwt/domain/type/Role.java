package com.example.chilling0613.oauth2jwt.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST"), USER("ROLE_USER");
    private final String key;
}

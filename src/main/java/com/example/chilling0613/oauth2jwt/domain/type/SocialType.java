package com.example.chilling0613.oauth2jwt.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialType {
    KAKAO("kakao"), NAVER("naver"), GOOGLE("google");

    private final String socialType;
}

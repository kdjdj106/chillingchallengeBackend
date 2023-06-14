package com.example.chilling0613.oauth2jwt.domain.dto;

import com.example.chilling0613.oauth2jwt.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private boolean loginSuccess;
    private User user;
    private String accessToken;
    private String RefreshToken;

    private boolean socialUser;
    private String userCode;
    private String email;

}

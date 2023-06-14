package com.example.chilling0613.oauth2jwt.service;

import com.example.chilling0613.oauth2jwt.domain.dto.SignupRequestDto;
import com.example.chilling0613.oauth2jwt.domain.dto.TokenDto;
import com.example.chilling0613.oauth2jwt.domain.entity.User;
import com.example.chilling0613.oauth2jwt.jwt.JwtProvider;
import com.example.chilling0613.oauth2jwt.repository.RefreshTokenRepository;
import com.example.chilling0613.oauth2jwt.repository.UserRepository;
import com.example.chilling0613.oauth2jwt.domain.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    public TokenDto login(User user) {

        // 토큰 발행
        TokenDto tokenDto = jwtProvider.generateTokenDto(user.getUsercode());
        // RefreshToken 만 DB에 저장
        // signup 시에도 저장하고, 로그인시에도 저장하므로 존재하는 토큰을 찾기 위해 key 값이 필요
        RefreshToken refreshToken = RefreshToken.builder()
                .key(user.getId())
                .token(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        System.out.println("토큰 발급과 저장을 완료했습니다.");
        return tokenDto;
    }

    public TokenDto formSignup(SignupRequestDto requestDto) {
        if(userRepository.findByEmail(requestDto.getEmail()).isPresent()){
            return null;
        }
        return jwtProvider.generateTokenDto(requestDto.getEmail());
    }
}

package com.example.chilling0613.oauth2jwt.service;

import com.example.chilling0613.oauth2jwt.domain.dto.*;
import com.example.chilling0613.oauth2jwt.domain.dto.kakao.KakaoAccessTokenDto;
import com.example.chilling0613.oauth2jwt.domain.dto.kakao.KakaoUserInfoDto;
import com.example.chilling0613.oauth2jwt.domain.entity.User;
import com.example.chilling0613.oauth2jwt.domain.type.Role;
import com.example.chilling0613.oauth2jwt.domain.type.SocialType;
import com.example.chilling0613.oauth2jwt.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    String KAKAO_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    String KAKAO_REDIRECT_URI;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    String KAKAO_SECRET_KEY;

    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public KakaoAccessTokenDto getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // body 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); //카카오 공식문서 기준 authorization_code 로 고정
        params.add("client_id", KAKAO_CLIENT_ID); //카카오 앱 REST API 키
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code); //인가 코드 요청시 받은 인가 코드값, 프론트에서 받아오는 그 코드
        params.add("client_secret", KAKAO_SECRET_KEY);

        // 헤더와 바디 합치기 위해 HttpEntity 객체 생성
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
        System.out.println(kakaoTokenRequest);

        // 카카오로부터 Access token 수신
        ResponseEntity<String> accessTokenResponse = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // JSON Parsing (-> KakaoTokenDto)
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoAccessTokenDto kakaoAccessTokenDto = null;
        try {
            kakaoAccessTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoAccessTokenDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoAccessTokenDto;
    }


    public User getKakaoInfo(String kakaoAccessToken) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);

        // POST 방식으로 API 서버에 요청 보내고, response 받아옴
        ResponseEntity<String> accountInfoResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                accountInfoRequest,
                String.class
        );
        System.out.println("카카오 서버에서 정상적으로 데이터를 수신했습니다.");
        // JSON Parsing (-> kakaoUserInfoDto)
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoUserInfoDto kakaoUserInfoDto = null;
        log.info(accountInfoResponse.getBody().toString());
        try {
            kakaoUserInfoDto = objectMapper.readValue(accountInfoResponse.getBody(), KakaoUserInfoDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // kakaoUserInfoDto 에서 필요한 정보 꺼내서 Account 객체로 매핑
        String email = kakaoUserInfoDto.getKakao_account().getEmail();
        String kakaoName = kakaoUserInfoDto.getKakao_account().getProfile().getNickname();
        String id = kakaoUserInfoDto.getId().toString();
        if (kakaoName == null) {
            kakaoName = UUID.randomUUID().toString();
        }
        return User.builder()
                .socialType(SocialType.KAKAO)
                .email(email)
                .nickname(kakaoName)
                .role(Role.USER)
                .socialId(id)
                .usercode(SocialType.KAKAO.getSocialType() + id)
                .build();
    }

    public ResponseEntity<LoginResponseDto> kakaoLogin(String kakaoAccessToken) {
        // kakaoAccessToken 으로 회원정보 받아오기
        User user = getKakaoInfo(kakaoAccessToken);
        // 만약 이미 회원가입 되어있다면 토큰값, 유저코드 반환
        if (userRepository.findByUsercode(user.getUsercode()).isPresent()) {
            TokenDto tokenDto = securityService.login(user);
            log.info("로그인이 확인됐고, 토큰을 발급했습니다. 토큰 값 : " + tokenDto.getAccessToken());
            return ResponseEntity.ok(LoginResponseDto.builder()
                    .loginSuccess(true)
                    .accessToken(tokenDto.getAccessToken())
                    .userCode(user.getUsercode())
                    .socialUser(true)
                    .email(user.getEmail())
                    .user(user)
                    .build());
        } else {
            // 없다면 회원가입 시키고 토큰값, 유저코드 반환
            log.info("회원 정보가 없습니다. 자동 회원가입을 진행합니다.");
            userRepository.save(user);
            TokenDto tokenDto = securityService.login(user);
            return ResponseEntity.ok(LoginResponseDto.builder()
                    .loginSuccess(true)
                    .accessToken(tokenDto.getAccessToken())
                    .userCode(user.getUsercode())
                    .socialUser(true)
                    .email(user.getEmail())
                    .user(user)
                    .build());
        }
    }

    public ResponseEntity<SignupResponseDto> formSignup(SignupRequestDto requestDto) {
        // 받아온 정보 DB에 저장
        if (userRepository.findByEmail(requestDto.getEmail()).isEmpty()) {
            User newUser = User.builder()
                    .email(requestDto.getEmail())
                    .nickname(requestDto.getNickName())
                    .password(bCryptPasswordEncoder.encode(requestDto.getPassword()))
                    .role(Role.USER)
                    .usercode(UUID.randomUUID().toString())
                    .build();
            userRepository.save(newUser);
            SignupResponseDto responseDto = SignupResponseDto.builder()
                    .signupSuccess(true)
                    .message("정상적으로 회원가입 되었습니다. 다시 로그인 해주세요")
                    .build();
            return ResponseEntity.ok().body(responseDto);
        } else {
            SignupResponseDto responseDto = SignupResponseDto.builder()
                    .signupSuccess(false)
                    .message("이미 가입 되어있는 회원입니다. 다시 로그인 해주세요")
                    .build();
            return ResponseEntity.ok().body(responseDto);
        }
    }

    public ResponseEntity<LoginResponseDto> formLogin(LoginRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            log.info("유저 이메일로 검색결과 있음");
            User user = userRepository.findByEmail(requestDto.getEmail()).get();
            log.info(bCryptPasswordEncoder.encode(requestDto.getPassword()));
            log.info((user.getPassword()));

            if (bCryptPasswordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
                log.info("유저 정보와 입력하신 이메일, 비밀번호가 일치합니다. ");
                log.info("로그인 중... 토큰 발행중...");
                TokenDto tokenDto = securityService.login(user);

                return ResponseEntity.ok(LoginResponseDto.builder()
                        .loginSuccess(true)
                        .accessToken(tokenDto.getAccessToken())
                        .RefreshToken(tokenDto.getRefreshToken())
                        .userCode(user.getUsercode())
                        .socialUser(false)
                        .email(user.getEmail())
                        .user(user)
                        .build());
            } else {
                log.error("비밀번호가 일치하지 않습니다.");
                return ResponseEntity.ok(LoginResponseDto.builder()
                        .loginSuccess(false)
                        .build());
            }
        } else {
            return ResponseEntity.ok(LoginResponseDto.builder()
                    .loginSuccess(false)
                    .build());
        }
    }
}

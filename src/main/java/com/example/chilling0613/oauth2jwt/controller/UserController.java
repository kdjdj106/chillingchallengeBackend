package com.example.chilling0613.oauth2jwt.controller;

import com.example.chilling0613.mission.domain.dto.*;
import com.example.chilling0613.mission.domain.service.MissionService;
import com.example.chilling0613.mission.domain.service.UserService;
import com.example.chilling0613.oauth2jwt.domain.dto.KakaoLoginRequestDto;
import com.example.chilling0613.oauth2jwt.domain.dto.LoginRequestDto;
import com.example.chilling0613.oauth2jwt.domain.dto.LoginResponseDto;
import com.example.chilling0613.oauth2jwt.domain.dto.SignupRequestDto;
import com.example.chilling0613.oauth2jwt.domain.entity.User;
import com.example.chilling0613.oauth2jwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserController {

    private final AuthService authService;
    private final UserService userService;
    private final MissionService missionService;

    // 로그인 db에 없으면 자동 회원가입 및 로그인
    @PostMapping("/login/kakao")
    public ResponseEntity<LoginResponseDto> kakaoLogin(@RequestBody KakaoLoginRequestDto dto){
        String kakaoAccessToken = authService.getKakaoAccessToken(dto.getCode()).getAccess_token();
        return authService.kakaoLogin(kakaoAccessToken);
    }


    @PostMapping("/signup/form")
    public ResponseEntity<?> formSignup(@RequestBody SignupRequestDto signupRequestDto){

        return authService.formSignup(signupRequestDto);
    }
    @PostMapping("/login/form")
    public ResponseEntity<LoginResponseDto> formLogin(@RequestBody LoginRequestDto requestDto){

        return authService.formLogin(requestDto);
    }



    @GetMapping("/showMyHistory")
    public ResponseEntity<List<ShowFeedToFrontDto>> getHistoryPage(@RequestParam(value = "code") String usercode,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {

        List<ShowFeedToFrontDto> myHistory = new ArrayList<>();
        myHistory = userService.getMyHistory(usercode);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, myHistory.size());

        if (startIndex > endIndex) {
            // 페이지 범위가 데이터 크기를 초과하는 경우 빈 결과 반환
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<ShowFeedToFrontDto> pageData = myHistory.subList(startIndex, endIndex);
        return ResponseEntity.ok(pageData);
    }

    @GetMapping("/showMyInfo")
    public ResponseEntity<UserInfoFormDto.Response>  showMyInfo(@RequestParam(value = "code") String usercode){
        UserInfoDto infoDto = userService.showMyInfo(usercode);

        return ResponseEntity.ok(new UserInfoFormDto.Response(
                infoDto.getNickname(),
                infoDto.getMissionCnt(),
                infoDto.getAttendance(),
                infoDto.getContinuous(),
                infoDto.getProfileImagePath()
        ));
    }

    @PutMapping("/update/nickname/{usercode}")
    public ResponseEntity<User> updateUserNickname(@PathVariable String usercode,
                                                   @RequestBody UserNicknameUpdateRequestDto request) {

        return ResponseEntity.ok(userService.updateUserNickname(usercode,request));
    }

    @PutMapping("/update/imageUrl/{usercode}")
    public ResponseEntity<User> updateUserImageUrl(@PathVariable String usercode,
                                                   @RequestBody UserImageUrlUpdateRequestDto request) {

        return ResponseEntity.ok(userService.updateUserImageUrl(usercode,request));
    }

    @GetMapping("/test/test")
    public String testtest(@RequestParam (value = "code") String code){
        System.out.println(code);
        return code;
    }



}

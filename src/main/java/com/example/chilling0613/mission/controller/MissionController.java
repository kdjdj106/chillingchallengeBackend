package com.example.chilling0613.mission.controller;

import com.example.chilling0613.mission.domain.dto.CompleteMissionToBackDto;
import com.example.chilling0613.mission.domain.service.MissionService;
import com.example.chilling0613.oauth2jwt.domain.dto.KakaoLoginRequestDto;
import com.example.chilling0613.oauth2jwt.domain.dto.LoginRequestDto;
import com.example.chilling0613.oauth2jwt.domain.dto.LoginResponseDto;
import com.example.chilling0613.oauth2jwt.domain.dto.SignupRequestDto;
import com.example.chilling0613.oauth2jwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mission")
public class MissionController {

    private final AuthService authService;
    private final MissionService missionService;


    @GetMapping("/test/test")
    public String testtest(@RequestParam (value = "code") String code){
        System.out.println(code);
        return code;
    }

    @PostMapping("/completeMission")
    public ResponseEntity<?> completeMission(@RequestBody List<CompleteMissionToBackDto> list) throws Exception {

        return ResponseEntity.ok( missionService.completeMission(list));
    }

}

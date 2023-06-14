package com.example.chilling0613.oauth2jwt.domain.entity;

import com.example.chilling0613.oauth2jwt.domain.type.Role;
import com.example.chilling0613.oauth2jwt.domain.type.SocialType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Table(name = "USER")
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long id;

    private String email; // 이메일
    private String password; // 비밀번호
    private String nickname; // 닉네임
    private String imageUrl; // 프로필 이미지
    private String usercode;

    @ColumnDefault("0")
    private int missionCnt; // 총 미션 수행 갯수


    private String attendance; // 출석 날짜들 (달력을 위해)

    @ColumnDefault("false")
    private boolean todayCheck; // 오늘 미션을 했는지 체크 밤 11시쯤 체크예정
    private LocalDate lastAttendance; // 마지막 출석 일자
    @ColumnDefault("0")
    private int continuous; // 연속출석일수



    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType; // KAKAO, NAVER, GOOGLE

    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)
    private String refreshToken; // 리프레시 토큰



    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.role = Role.USER;
    }
    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }

}
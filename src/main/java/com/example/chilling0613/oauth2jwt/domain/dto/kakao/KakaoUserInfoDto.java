package com.example.chilling0613.oauth2jwt.domain.dto.kakao;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Properties;
@Data
@NoArgsConstructor
public class KakaoUserInfoDto {

    /*

    @sierrah
    [Kakao] 현재 mument 에서의 동의 항목
    필수 - 닉네임 (profile_nickname)
    선택 - 카카오계정 이메일 (account_email)

    카카오 개발자 문서에 표기된 JSON 서식을 보고 만들었다.

    */

    private Long id; //회원번호, *Required*
    private String connected_at; //서비스에 연결된 시각, UTC*
    private Properties properties;
    private KakaoUserInfo kakao_account;
    @Data
    public class Properties{
        private String nickname;
        private String profile_image;
        private String thumbnail_image;
    }
    @Data
    public class KakaoUserInfo {

        private Boolean profile_nickname_needs_agreement;
        private Boolean profile_image_needs_agreement;
        private KakaoProfile profile;
        private Boolean has_email;
        private Boolean email_needs_agreement;
        private Boolean is_email_valid;
        private Boolean is_email_verified;
        private String email;



        @Data
        public class KakaoProfile {
            private String nickname;
            private String thumbnail_image_url;
            private String profile_image_url;
            private String is_default_image;
        }
    }
}

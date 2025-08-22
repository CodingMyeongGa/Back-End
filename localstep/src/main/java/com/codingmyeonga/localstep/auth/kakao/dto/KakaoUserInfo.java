package com.codingmyeonga.localstep.auth.kakao.dto;

import lombok.Data;

@Data
public class KakaoUserInfo {
    private Long id;
    private KakaoAccount kakao_account;

    @Data
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Data
        public static class Profile {
            private String nickname;
        }
    }
}

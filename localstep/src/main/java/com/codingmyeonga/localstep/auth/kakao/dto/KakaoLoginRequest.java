package com.codingmyeonga.localstep.auth.kakao.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KakaoLoginRequest {
    @NotBlank
    private String accessToken;
}

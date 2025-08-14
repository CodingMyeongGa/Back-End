package com.codingmyeonga.localstep.auth.service;

import com.codingmyeonga.localstep.auth.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    public SignupResponse signup(SignupRequest request) {
        // TODO: 회원가입 로직
        return new SignupResponse(true, "회원가입 성공");
    }

    public LoginResponse login(LoginRequest request) {
        // TODO: 로그인 로직
        return new LoginResponse("sample-jwt-token");
    }

    public EmailCheckResponse checkEmail(EmailCheckRequest request) {
        // TODO: 이메일 중복 확인 로직
        return new EmailCheckResponse(false);
    }

    public void logout(String token) {
        // TODO: 로그아웃 로직
    }

    public KakaoTokenResponse getKakaoToken(String code) {
        // TODO: 카카오 API로 토큰 발급 로직
        return new KakaoTokenResponse("kakao-access-token");
    }

    public LoginResponse kakaoLogin(String accessToken) {
        // TODO: 카카오 로그인 로직
        return new LoginResponse("sample-jwt-token");
    }
}

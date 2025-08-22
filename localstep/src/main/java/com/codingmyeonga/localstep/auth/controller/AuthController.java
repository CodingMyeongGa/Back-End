package com.codingmyeonga.localstep.auth.controller;

import com.codingmyeonga.localstep.auth.dto.*;
import com.codingmyeonga.localstep.auth.kakao.dto.*;
import com.codingmyeonga.localstep.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/check-email")
    public ResponseEntity<EmailCheckResponse> checkEmail(@Valid @RequestBody EmailCheckRequest request) {
        return ResponseEntity.ok(authService.checkEmail(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("로그아웃 성공 (토큰은 클라이언트에서 삭제하세요)");
    }

    @PostMapping("/kakao/token")
    public ResponseEntity<KakaoTokenResponse> getKakaoToken(@RequestParam("code") String code) {
        return ResponseEntity.ok(authService.getKakaoTokenByCode(code));
    }

    @PostMapping("/kakao/login")
    public ResponseEntity<LoginResponse> kakaoLogin(@Valid @RequestBody KakaoLoginRequest request) {
        return ResponseEntity.ok(authService.kakaoLoginByAccessToken(request.getAccessToken()));
    }

    @PostMapping("/kakao")
    public ResponseEntity<LoginResponse> kakaoOneStep(@RequestParam("code") String code) {
        return ResponseEntity.ok(authService.kakaoLoginByCode(code));
    }
}
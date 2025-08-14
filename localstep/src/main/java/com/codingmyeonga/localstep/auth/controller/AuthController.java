package com.codingmyeonga.localstep.auth.controller;

import com.codingmyeonga.localstep.auth.dto.*;
import com.codingmyeonga.localstep.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/check-email")
    public ResponseEntity<EmailCheckResponse> checkEmail(@RequestBody EmailCheckRequest request) {
        return ResponseEntity.ok(authService.checkEmail(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/kakao/token")
    public ResponseEntity<KakaoTokenResponse> getKakaoToken(@RequestParam String code) {
        return ResponseEntity.ok(authService.getKakaoToken(code));
    }

    @PostMapping("/kakao/login")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam String accessToken) {
        return ResponseEntity.ok(authService.kakaoLogin(accessToken));
    }
}

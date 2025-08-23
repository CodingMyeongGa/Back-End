package com.codingmyeonga.localstep.auth.controller;

import com.codingmyeonga.localstep.auth.dto.*;
import com.codingmyeonga.localstep.auth.kakao.dto.*;
import com.codingmyeonga.localstep.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "회원가입, 로그인, 이메일 중복 확인, 카카오 로그인 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "이메일, 비밀번호, user_id(닉네임)으로 회원가입합니다.")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일, 비밀번호를 사용하여 로그인하고 JWT 토큰을 발급받습니다.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/check-email")
    @Operation(summary = "이메일 중복 확인", description = "입력한 이메일이 이미 사용 중인지 확인합니다.")
    public ResponseEntity<EmailCheckResponse> checkEmail(@Valid @RequestBody EmailCheckRequest request) {
        return ResponseEntity.ok(authService.checkEmail(request));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "클라이언트에서 JWT 토큰을 삭제하여 로그아웃합니다.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("로그아웃 성공 (토큰은 클라이언트에서 삭제하세요)");
    }

    @PostMapping("/kakao/token")
    @Operation(summary = "카카오 토큰 발급", description = "인가 코드를 이용하여 카카오 액세스 토큰을 발급받습니다.")
    public ResponseEntity<KakaoTokenResponse> getKakaoToken(@RequestParam("code") String code) {
        return ResponseEntity.ok(authService.getKakaoTokenByCode(code));
    }

    @PostMapping("/kakao/login")
    @Operation(summary = "카카오 로그인 (AccessToken)", description = "카카오 AccessToken을 사용하여 로그인/회원가입을 처리합니다.")
    public ResponseEntity<LoginResponse> kakaoLogin(@Valid @RequestBody KakaoLoginRequest request) {
        return ResponseEntity.ok(authService.kakaoLoginByAccessToken(request.getAccessToken()));
    }

    @PostMapping("/kakao")
    @Operation(summary = "카카오 원스텝 로그인", description = "인가 코드를 직접 받아 로그인/회원가입을 한 번에 처리합니다.")
    public ResponseEntity<LoginResponse> kakaoOneStep(@RequestParam("code") String code) {
        return ResponseEntity.ok(authService.kakaoLoginByCode(code));
    }
}
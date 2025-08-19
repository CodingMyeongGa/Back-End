package com.codingmyeonga.localstep.auth.service;

import com.codingmyeonga.localstep.auth.dto.*;
import com.codingmyeonga.localstep.auth.entity.User;
import com.codingmyeonga.localstep.auth.exception.ApiException;
import com.codingmyeonga.localstep.auth.jwt.JwtTokenProvider;
import com.codingmyeonga.localstep.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new ApiException(HttpStatus.CONFLICT.value(), "이미 존재하는 이메일입니다.");
        });

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        userRepository.save(user);
        return new SignupResponse(true, "회원가입 성공");
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED.value(), "존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED.value(), "비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new LoginResponse(token);
    }

    public EmailCheckResponse checkEmail(EmailCheckRequest request) {
        boolean exists = userRepository.findByEmail(request.getEmail()).isPresent();
        return new EmailCheckResponse(exists);
    }

    public void logout(String token) {
        // JWT 자체는 서버 강제 만료 불가.
        // 필요 시 Redis(블랙리스트)를 도입해 남은 만료 기간 동안 차단.
        // 현재는 프론트에서 토큰 삭제 방식 권장.
    }

    // TODO: 카카오 OAuth2 연동은 추후 구현
    public KakaoTokenResponse getKakaoToken(String code) {
        return new KakaoTokenResponse("kakao-access-token");
    }

    public LoginResponse kakaoLogin(String accessToken) {
        String token = jwtTokenProvider.generateToken("kakao-user@example.com");
        return new LoginResponse(token);
    }
}

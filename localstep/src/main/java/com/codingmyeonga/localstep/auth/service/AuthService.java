package com.codingmyeonga.localstep.auth.service;

import com.codingmyeonga.localstep.auth.dto.*;
import com.codingmyeonga.localstep.auth.jwt.JwtTokenProvider;
import com.codingmyeonga.localstep.auth.kakao.KakaoProperties;
import com.codingmyeonga.localstep.auth.kakao.dto.*;
import com.codingmyeonga.localstep.auth.entity.User;
import com.codingmyeonga.localstep.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;
    private final KakaoProperties kakaoProperties;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new SignupResponse(false, "이미 존재하는 이메일입니다.");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();
        userRepository.save(user);
        return new SignupResponse(true, "회원가입 성공");
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        return new LoginResponse(jwtTokenProvider.generateToken(user.getEmail()));
    }

    @Transactional(readOnly = true)
    public EmailCheckResponse checkEmail(EmailCheckRequest request) {
        boolean exists = userRepository.existsByEmail(request.getEmail());
        return new EmailCheckResponse(exists);
    }

    @Transactional
    public KakaoTokenResponse getKakaoTokenByCode(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> form = new HashMap<>();
        form.put("grant_type", "authorization_code");
        form.put("client_id", kakaoProperties.getClientId());
        form.put("redirect_uri", kakaoProperties.getRedirectUri());
        form.put("code", code);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(form, headers);

        ResponseEntity<KakaoTokenResponse> response =
                restTemplate.postForEntity(kakaoProperties.getTokenUri(), request, KakaoTokenResponse.class);

        return response.getBody();
    }

    @Transactional
    public LoginResponse kakaoLoginByAccessToken(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                kakaoProperties.getUserInfoUri(),
                HttpMethod.GET,
                request,
                KakaoUserInfo.class
        );

        KakaoUserInfo kakao = response.getBody();
        String email = (kakao.getKakao_account().getEmail() != null)
                ? kakao.getKakao_account().getEmail()
                : "kakao_" + kakao.getId() + "@kakao.local";

        String nickname = kakao.getKakao_account().getProfile().getNickname();

        User user = userRepository.findByEmail(email).orElseGet(() ->
                userRepository.save(User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .nickname(nickname)
                        .build())
        );

        return new LoginResponse(jwtTokenProvider.generateToken(user.getEmail()));
    }

    @Transactional
    public LoginResponse kakaoLoginByCode(String code) {
        KakaoTokenResponse token = getKakaoTokenByCode(code);
        return kakaoLoginByAccessToken(token.getAccessToken());
    }

    public UserResponse getMyInfo(String token) {
        String jwt = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(jwt);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );
    }
}
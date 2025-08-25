package com.codingmyeonga.localstep.config;

import com.codingmyeonga.localstep.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // OPTIONS 요청 허용
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 허용할 Origin 설정 (프론트엔드 도메인들)
        configuration.addAllowedOrigin("http://localhost:5173"); // Vite 기본 포트
        configuration.addAllowedOrigin("http://localhost:5174"); // Vite 대안 포트
        configuration.addAllowedOrigin("http://localhost:3000"); // React 기본 포트
        configuration.addAllowedOrigin("http://localhost:8080"); // 백엔드 포트
        configuration.addAllowedOrigin("http://43.201.15.212:8080"); // 서버 IP
        configuration.addAllowedOrigin("http://43.201.15.212"); // 서버 IP (포트 없음)
        configuration.addAllowedOrigin("https://43.201.15.212"); // HTTPS 서버 IP
        configuration.addAllowedOrigin("https://43.201.15.212:8080"); // HTTPS 서버 IP + 포트
        
        // TODO: 프론트엔드 배포 도메인 확정 시 아래 줄 추가
        // configuration.addAllowedOrigin("https://your-frontend-domain.com");
        
        // 임시: 모든 Origin 허용 (문제 해결 후 제거)
        // configuration.addAllowedOriginPattern("*");
        
        // 허용할 HTTP 메서드 설정
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용 (OPTIONS 포함)
        
        // 허용할 헤더 설정
        configuration.addAllowedHeader("*");
        configuration.addAllowedHeader("Authorization");
        configuration.addAllowedHeader("Content-Type");
        configuration.addAllowedHeader("Accept");
        configuration.addAllowedHeader("Origin");
        configuration.addAllowedHeader("X-Requested-With");
        
        // 인증 정보 허용 (쿠키, Authorization 헤더 등)
        configuration.setAllowCredentials(true);
        
        // 노출할 헤더 설정
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Content-Type");
        configuration.addExposedHeader("Access-Control-Allow-Origin");
        configuration.addExposedHeader("Access-Control-Allow-Credentials");
        
        // Preflight 요청 캐시 시간 설정 (개발 중에는 제거 권장)
        // configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }


}

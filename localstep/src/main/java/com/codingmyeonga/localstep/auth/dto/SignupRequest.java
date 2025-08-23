package com.codingmyeonga.localstep.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class SignupRequest {
    @Email @NotBlank
    private String email;

    @NotBlank @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;

    @NotBlank @Size(min = 2, max = 20)
    private String nickname;
}

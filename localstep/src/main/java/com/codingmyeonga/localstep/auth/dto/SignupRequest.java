package com.codingmyeonga.localstep.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class SignupRequest {
    private String email;
    private String password;
    private String nickname;
}

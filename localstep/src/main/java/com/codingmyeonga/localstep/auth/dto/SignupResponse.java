package com.codingmyeonga.localstep.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class SignupResponse {
    private boolean success;
    private String message;
}

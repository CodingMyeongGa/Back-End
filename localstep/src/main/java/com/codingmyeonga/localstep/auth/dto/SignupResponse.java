package com.codingmyeonga.localstep.auth.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class SignupResponse {
    private boolean success;
    private String message;
}
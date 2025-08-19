package com.codingmyeonga.localstep.auth.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class EmailCheckResponse {
    private boolean exists;
}
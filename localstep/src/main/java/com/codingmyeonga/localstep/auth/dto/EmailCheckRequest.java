package com.codingmyeonga.localstep.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class EmailCheckRequest {
    @Email @NotBlank
    private String email;
}

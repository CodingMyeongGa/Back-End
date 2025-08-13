package com.codingmyeonga.localstep.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreVisitTestResponseDto {

    private Integer visitId;
    private Integer pointsAwarded;
    private String message;
}

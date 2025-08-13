package com.codingmyeonga.localstep.points.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointBalanceResponseDto {

    private Integer userId;
    private Integer totalPoints;
}

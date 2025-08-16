package com.codingmyeonga.localstep.points.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointBalanceResponseDto {

    @JsonProperty("user_id")
    private Integer userId;
    
    @JsonProperty("total_points")
    private Integer totalPoints;
}

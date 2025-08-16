package com.codingmyeonga.localstep.points.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointHistoryResponseDto {

    @JsonProperty("point_id")
    private Integer pointId;
    
    @JsonProperty("reason")
    private String reason;
    
    @JsonProperty("points")
    private Integer points;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}

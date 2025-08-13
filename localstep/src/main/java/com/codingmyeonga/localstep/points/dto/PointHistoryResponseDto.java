package com.codingmyeonga.localstep.points.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryResponseDto {

    private Integer pointId;
    private String reason;
    private Integer points;
    private LocalDateTime createdAt;
}

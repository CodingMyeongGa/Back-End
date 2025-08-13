package com.codingmyeonga.localstep.points.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StepGoalRewardResponseDto {

    private Integer goalId;
    private Integer goalSteps;
    private Integer currentSteps;
    private Boolean goalAchieved;
    private Integer pointsAwarded;
    private String message;
}

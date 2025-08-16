package com.codingmyeonga.localstep.points.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StepGoalRewardResponseDto {

    @JsonProperty("goal_id")
    private Integer goalId;
    
    @JsonProperty("goal_steps")
    private Integer goalSteps;
    
    @JsonProperty("current_steps")
    private Integer currentSteps;
    
    @JsonProperty("goal_achieved")
    private Boolean goalAchieved;
    
    @JsonProperty("points_awarded")
    private Integer pointsAwarded;
    
    private String message;
}

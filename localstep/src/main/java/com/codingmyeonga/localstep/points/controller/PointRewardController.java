package com.codingmyeonga.localstep.points.controller;

import com.codingmyeonga.localstep.points.dto.StepGoalRewardRequestDto;
import com.codingmyeonga.localstep.points.dto.StepGoalRewardResponseDto;
import com.codingmyeonga.localstep.points.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/points/reward")
@RequiredArgsConstructor
public class PointRewardController {

    private final PointService pointService;

    @PostMapping("/steps")
    public ResponseEntity<StepGoalRewardResponseDto> rewardStepGoal(@RequestBody StepGoalRewardRequestDto requestDto) {
        
        StepGoalRewardResponseDto response = pointService.rewardStepGoal(requestDto);
        return ResponseEntity.ok(response);
    }
}

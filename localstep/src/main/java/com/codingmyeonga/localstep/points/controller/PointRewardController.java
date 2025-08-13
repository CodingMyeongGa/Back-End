package com.codingmyeonga.localstep.points.controller;

import com.codingmyeonga.localstep.points.dto.StepGoalRewardRequestDto;
import com.codingmyeonga.localstep.points.dto.StepGoalRewardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/points/reward")
@RequiredArgsConstructor
public class PointRewardController {

    @PostMapping("/steps")
    public ResponseEntity<StepGoalRewardResponseDto> rewardStepGoal(@RequestBody StepGoalRewardRequestDto requestDto) {
        
        // TODO: 서비스 로직 구현
        return ResponseEntity.ok().build();
    }
}

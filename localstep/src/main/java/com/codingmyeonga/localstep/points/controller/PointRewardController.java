package com.codingmyeonga.localstep.points.controller;

import com.codingmyeonga.localstep.points.dto.StepGoalRewardRequestDto;
import com.codingmyeonga.localstep.points.dto.StepGoalRewardResponseDto;
import com.codingmyeonga.localstep.points.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/points/reward")
@RequiredArgsConstructor
@Tag(name = "포인트 지급 관련")
public class PointRewardController {

    private final PointService pointService;

    @PostMapping("/steps")
    @Operation(summary = "걸음 수 목표 달성 포인트 지급", description = "사용자의 걸음 목표 달성에 대한 포인트 보상을 지급합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "보상 지급 성공",
            content = @Content(schema = @Schema(implementation = StepGoalRewardResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "이미 보상을 받은 목표"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    })
    public ResponseEntity<StepGoalRewardResponseDto> rewardStepGoal(
            @Parameter(description = "걸음 목표 보상 요청 정보", required = true) 
            @RequestBody StepGoalRewardRequestDto requestDto) {
        
        StepGoalRewardResponseDto response = pointService.rewardStepGoal(requestDto);
        return ResponseEntity.ok(response);
    }
}

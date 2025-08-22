package com.codingmyeonga.localstep.steps.controller;

import com.codingmyeonga.localstep.steps.dto.StepsDto.*;
import com.codingmyeonga.localstep.steps.service.StepsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "StepsAPI", description = "걸음 수 측정 API")
@RestController
@RequestMapping("/api/steps")
public class StepsController {

    private final StepsService stepsService;

    public StepsController(StepsService stepsService) {
        this.stepsService = stepsService;
    }

    @Operation(
            summary = "누적 걸음 수 데이터 수신",
            description = "모바일 앱에서 측정된 걸음 수 데이터를 수신하여 서버에 전달"
    )
    @PostMapping("/data")
    public ResponseEntity<StepsResponse> postSteps(
            @RequestBody PostStepsRequest request
    ) {
        return ResponseEntity.ok(stepsService.createSteps(request));
    }

    @Operation(
            summary = "누적 걸음 수 저장",
            description = "수신된 걸음 수를 일자별로 저장"
    )
    @PutMapping("/data/{user_id}")
    public ResponseEntity<StepsResponse> putSteps(
            @Parameter(description = "사용자 ID", example = "1", required = true)
            @PathVariable("user_id") Long userId,
            @RequestBody PutStepsRequest request
    ) {
        return ResponseEntity.ok(stepsService.updateSteps(userId, request));
    }

    @Operation(
            summary = "목표 걸음 수 설정",
            description = "사용자가 하루 목표 걸음 수를 설정"
    )
    @PostMapping("/goal")
    public ResponseEntity<GoalResponse> postGoal(
            @RequestBody PostGoalRequest request
    ) {
        return ResponseEntity.ok(stepsService.setGoal(request));
    }

    @Operation(
            summary = "목표 걸음 수 조회",
            description = "사용자가 현재 설정한 목표를 조회"
    )
    @GetMapping("/goal/{user_id}")
    public ResponseEntity<GoalResponse> getGoal(
            @Parameter(description = "사용자 ID", example = "1", required = true)
            @PathVariable("user_id") Long userId
    ) {
        return ResponseEntity.ok(stepsService.getGoal(userId));
    }

    @Operation(
            summary = "날짜별 걸음 수 조회",
            description = "사용자가 날짜별 걸음 수를 조회. 날짜 형식은 yyyy-MM-dd, 구간은 [startDate, endDate]"
    )
    @GetMapping("/history/{user_id}")
    public ResponseEntity<HistoryResponse> getHistory(
            @Parameter(description = "사용자 ID", example = "1", required = true)
            @PathVariable("user_id") Long userId,
            @Parameter(description = "조회 시작일(yyyy-MM-dd)", example = "2025-08-01", required = true)
            @RequestParam("startDate") String startDate,
            @Parameter(description = "조회 종료일(yyyy-MM-dd)", example = "2025-08-22", required = true)
            @RequestParam("endDate") String endDate
    ) {
        return ResponseEntity.ok(stepsService.getHistory(userId, startDate, endDate));
    }
}

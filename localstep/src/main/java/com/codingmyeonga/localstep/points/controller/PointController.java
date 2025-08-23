package com.codingmyeonga.localstep.points.controller;

import com.codingmyeonga.localstep.points.dto.PointBalanceResponseDto;
import com.codingmyeonga.localstep.points.dto.PointHistoryResponseDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "포인트 지급 관련")
public class PointController {

    private final PointService pointService;

    /**
     * 사용자의 포인트 잔액을 조회합니다.
     * @param userId 사용자 ID
     * @return 포인트 잔액 정보
     */
    @GetMapping("/{user_id}/points/balance")
    @Operation(summary = "포인트 잔액 조회", description = "사용자의 현재 포인트 잔액을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "포인트 잔액 조회 성공",
            content = @Content(schema = @Schema(implementation = PointBalanceResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    })
    public ResponseEntity<PointBalanceResponseDto> getPointBalance(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable("user_id") Long userId) {
        
        PointBalanceResponseDto balance = pointService.getPointBalance(userId);
        return ResponseEntity.ok(balance);
    }
    
    /**
     * 사용자의 포인트 지급/차감 내역을 조회합니다.
     * @param userId 사용자 ID
     * @param startDate 시작 날짜 (선택사항)
     * @param endDate 종료 날짜 (선택사항)
     * @return 포인트 히스토리 목록
     */
    @GetMapping("/{user_id}/points/history")
    @Operation(summary = "포인트 지급/차감 내역 조회", description = "사용자의 포인트 지급/차감 내역을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "포인트 히스토리 조회 성공",
            content = @Content(schema = @Schema(implementation = PointHistoryResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 날짜 형식"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    })
    public ResponseEntity<List<PointHistoryResponseDto>> getPointHistory(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable("user_id") Long userId,
            @Parameter(description = "시작 날짜 (yyyy-MM-dd 형식)", example = "2024-01-01") 
            @RequestParam(value = "start_date", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "종료 날짜 (yyyy-MM-dd 형식)", example = "2024-12-31") 
            @RequestParam(value = "end_date", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        List<PointHistoryResponseDto> history = pointService.getPointHistory(userId, startDate, endDate);
        return ResponseEntity.ok(history);
    }
}

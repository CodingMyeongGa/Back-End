package com.codingmyeonga.localstep.users.controller;

import com.codingmyeonga.localstep.users.dto.LocationRequestDto;
import com.codingmyeonga.localstep.users.dto.LocationResponseDto;
import com.codingmyeonga.localstep.users.dto.StoreVisitResponseDto;

import com.codingmyeonga.localstep.users.service.StoreVisitService;
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
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "포인트 지급 관련")
public class StoreVisitController {

    private final StoreVisitService storeVisitService;

    @GetMapping("/{user_id}/visits")
    @Operation(summary = "사용자 방문 기록 조회", description = "특정 사용자의 매장 방문 기록을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "방문 기록 조회 성공",
            content = @Content(schema = @Schema(implementation = StoreVisitResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    })
    public ResponseEntity<List<StoreVisitResponseDto>> getVisits(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable("user_id") Long userId,
            @Parameter(description = "시작 날짜 (yyyy-MM-dd 형식)", example = "2024-01-01") 
            @RequestParam(value = "start_date", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "종료 날짜 (yyyy-MM-dd 형식)", example = "2024-12-31") 
            @RequestParam(value = "end_date", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        List<StoreVisitResponseDto> visits = storeVisitService.getUserVisits(userId, startDate, endDate);
        return ResponseEntity.ok(visits);
    }

    @PostMapping("/location")
    @Operation(summary = "위치 데이터 전송 및 상점 방문 자동 포인트 지급", description = "사용자의 현재 위치를 제출하고 자동으로 매장 방문을 처리합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "위치 제출 및 방문 처리 성공",
            content = @Content(schema = @Schema(implementation = LocationResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 위치 정보"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    })
    public ResponseEntity<LocationResponseDto> submitLocation(
            @Parameter(description = "위치 정보", required = true) 
            @RequestBody LocationRequestDto requestDto) {
        
        LocationResponseDto response = storeVisitService.processLocationAndAutoVisit(requestDto);
        return ResponseEntity.ok(response);
    }

}

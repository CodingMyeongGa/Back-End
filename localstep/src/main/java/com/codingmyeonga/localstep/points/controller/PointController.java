package com.codingmyeonga.localstep.points.controller;

import com.codingmyeonga.localstep.points.dto.PointBalanceResponseDto;
import com.codingmyeonga.localstep.points.dto.PointHistoryResponseDto;
import com.codingmyeonga.localstep.points.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    /**
     * 사용자의 포인트 잔액을 조회합니다.
     * @param userId 사용자 ID
     * @return 포인트 잔액 정보
     */
    @GetMapping("/{user_id}/points/balance")
    public ResponseEntity<PointBalanceResponseDto> getPointBalance(
            @PathVariable("user_id") Integer userId) {
        
        PointBalanceResponseDto balance = pointService.getPointBalance(userId);
        return ResponseEntity.ok(balance);
    }
    
    /**
     * 사용자의 포인트 지급/차감 내역을 조회합니다.
     * @param userId 사용자 ID
     * @return 포인트 히스토리 목록
     */
    @GetMapping("/{user_id}/points/history")
    public ResponseEntity<List<PointHistoryResponseDto>> getPointHistory(
            @PathVariable("user_id") Integer userId) {
        
        List<PointHistoryResponseDto> history = pointService.getPointHistory(userId);
        return ResponseEntity.ok(history);
    }
}

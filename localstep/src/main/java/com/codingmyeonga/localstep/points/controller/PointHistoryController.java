package com.codingmyeonga.localstep.points.controller;

import com.codingmyeonga.localstep.points.dto.PointBalanceResponseDto;
import com.codingmyeonga.localstep.points.dto.PointHistoryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PointHistoryController {

    @GetMapping("/{user_id}/points/history")
    public ResponseEntity<List<PointHistoryResponseDto>> getPointHistory(
            @PathVariable("user_id") Integer userId,
            @RequestParam(value = "start_date", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "end_date", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        // TODO: 서비스 로직 구현
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{user_id}/points/balance")
    public ResponseEntity<PointBalanceResponseDto> getPointBalance(@PathVariable("user_id") Integer userId) {
        
        // TODO: 서비스 로직 구현
        return ResponseEntity.ok().build();
    }
}

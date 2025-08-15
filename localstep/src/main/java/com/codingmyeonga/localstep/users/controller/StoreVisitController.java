package com.codingmyeonga.localstep.users.controller;

import com.codingmyeonga.localstep.users.dto.LocationRequestDto;
import com.codingmyeonga.localstep.users.dto.LocationResponseDto;
import com.codingmyeonga.localstep.users.dto.StoreVisitResponseDto;
import com.codingmyeonga.localstep.users.dto.StoreVisitTestRequestDto;
import com.codingmyeonga.localstep.users.dto.StoreVisitTestResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class StoreVisitController {

    @GetMapping("/{user_id}/visits")
    public ResponseEntity<List<StoreVisitResponseDto>> getVisits(
            @PathVariable("user_id") Integer userId,
            @RequestParam(value = "start_date", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "end_date", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        // TODO: 서비스 로직 구현
        return ResponseEntity.ok().build();
    }

    @PostMapping("/location")
    public ResponseEntity<LocationResponseDto> submitLocation(@RequestBody LocationRequestDto requestDto) {
        
        // TODO: 서비스 로직 구현
        return ResponseEntity.ok().build();
    }

    @PostMapping("/visits")
    public ResponseEntity<StoreVisitTestResponseDto> createVisitTest(@RequestBody StoreVisitTestRequestDto requestDto) {
        
        // TODO: 서비스 로직 구현 (테스트용 - 배포 시 삭제 예정)
        return ResponseEntity.ok().build();
    }
}

package com.codingmyeonga.localstep.users.controller;

import com.codingmyeonga.localstep.users.dto.LocationRequestDto;
import com.codingmyeonga.localstep.users.dto.LocationResponseDto;
import com.codingmyeonga.localstep.users.dto.StoreVisitResponseDto;
import com.codingmyeonga.localstep.users.dto.StoreVisitTestRequestDto;
import com.codingmyeonga.localstep.users.dto.StoreVisitTestResponseDto;
import com.codingmyeonga.localstep.users.service.StoreVisitService;
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

    private final StoreVisitService storeVisitService;

    @GetMapping("/{user_id}/visits")
    public ResponseEntity<List<StoreVisitResponseDto>> getVisits(
            @PathVariable("user_id") Long userId,
            @RequestParam(value = "start_date", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "end_date", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        List<StoreVisitResponseDto> visits = storeVisitService.getUserVisits(userId, startDate, endDate);
        return ResponseEntity.ok(visits);
    }

    @PostMapping("/location")
    public ResponseEntity<LocationResponseDto> submitLocation(@RequestBody LocationRequestDto requestDto) {
        
        LocationResponseDto response = storeVisitService.processLocationAndAutoVisit(requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/visits")
    public ResponseEntity<StoreVisitTestResponseDto> createVisitTest(@RequestBody StoreVisitTestRequestDto requestDto) {
        
        StoreVisitTestResponseDto response = storeVisitService.createVisit(requestDto);
        return ResponseEntity.ok(response);
    }
}

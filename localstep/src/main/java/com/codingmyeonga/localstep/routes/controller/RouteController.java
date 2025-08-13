package com.codingmyeonga.localstep.routes.controller;

import com.codingmyeonga.localstep.routes.dto.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {
    //신규 루트 생성
    @PostMapping("/generate")
    public ResponseEntity<RouteResponse> generate(@Valid @RequestBody GenerateRouteRequest req) {
        // TODO: 서비스 연결
        return ResponseEntity.status(HttpStatus.CREATED).body(
                RouteResponse.builder()
                        .route_id(1L)
                        .user_id(req.getUser_id())
                        .user_lat(req.getUser_lat())
                        .user_lng(req.getUser_lng())
                        .goal_steps(req.getGoal_steps())
                        .created_at(LocalDateTime.now())
                        .is_completed(false)
                        .stores(List.of())
                        .build()
        );
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<List<RouteResponse>> getByDate(
            @PathVariable("user_id") Long userId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(List.of()); // TODO
    }

    @PatchMapping("/{route_id}/complete")
    public ResponseEntity<RouteResponse> complete(
            @PathVariable("route_id") Long routeId,
            @Valid @RequestBody CompleteRouteRequest req) {
        return ResponseEntity.ok(new RouteResponse()); // TODO
    }

    @GetMapping("/{route_id}/store/{store_id}")
    public ResponseEntity<StoreInRouteResponse> storeDetail(
            @PathVariable("route_id") Long routeId,
            @PathVariable("store_id") Long storeId) {
        return ResponseEntity.ok(new StoreInRouteResponse()); // TODO
    }
}

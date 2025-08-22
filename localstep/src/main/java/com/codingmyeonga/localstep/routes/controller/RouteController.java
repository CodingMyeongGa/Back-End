package com.codingmyeonga.localstep.routes.controller;

import com.codingmyeonga.localstep.routes.dto.*;
import com.codingmyeonga.localstep.routes.service.RouteService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;
    //신규 루트 생성
    @PostMapping("/generate")
    public ResponseEntity<RouteResponse> generate(@Valid @RequestBody GenerateRouteRequest req) {
        RouteResponse res = routeService.generate(req);
        return ResponseEntity.status(201).body(res);
    }
    //날짜별 조회
    @GetMapping("/user/{user_id}")
    public ResponseEntity<List<RouteResponse>> getByDate(@PathVariable("user_id") Long userId,@RequestParam("date") String dateStr) {
        // 문자열 날짜 -> LocalDate 변환
        LocalDate date = LocalDate.parse(dateStr);

        // 서비스 호출-> 데이터 가져오기
        List<RouteResponse> list = routeService.getByUserAndDate(userId, date);

        // 200 OK와 함께 데이터 응답
        return ResponseEntity.ok(list);
    }
    // 루트 완료 처리
    @PatchMapping("/{route_id}/complete")
    public ResponseEntity<RouteResponse> complete(
            @PathVariable("route_id") Long route_id,
            @Valid @RequestBody CompleteRouteRequest req) {
        RouteResponse res = routeService.complete(route_id);
        return ResponseEntity.ok(res);
    }

     //루트 내 상점 상세 정보 조회
    @GetMapping("/{route_id}/store/{order_in_route}")
    public ResponseEntity<StoreInRouteResponse> storeDetail(
            @PathVariable("route_id") Long routeId,
            @PathVariable("order_in_route") Integer storeId) {

        StoreInRouteResponse res = routeService.getStoreDetail(routeId, storeId);
        return ResponseEntity.ok(res);
    }

}

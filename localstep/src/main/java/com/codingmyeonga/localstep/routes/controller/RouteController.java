package com.codingmyeonga.localstep.routes.controller;

import com.codingmyeonga.localstep.routes.dto.*;
import com.codingmyeonga.localstep.routes.service.RouteService;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@Tag(name = "ai 산책 루트 생성/조회/완료처리", description = "산책 루트 생성,조회,완료 기능 / 루트 내 포함된 상세 상점 조회 기능")
public class RouteController {
    private final RouteService routeService;
    //신규 루트 생성
    @PostMapping("/generate")
    @Operation(summary = "산책 루트 생성", description = """
    - 사용자의 현재 위치(user_lat, user_lng)와 목표 걸음 수(goal_steps)를 기준으로 새로운 산책 루트를 생성
    - 상점 중 k개의 경유지를 뽑아 리턴(이 때 k는 걸음수/700의 몫이며, 만약 걸음수가 700미만이라면 기본값으로 2를 넣음)
    """
    )
    public ResponseEntity<RouteResponse> generate(@Valid @RequestBody GenerateRouteRequest req) {
        RouteResponse res = routeService.generate(req);
        return ResponseEntity.status(201).body(res);
    }
    //날짜별 조회
    @GetMapping("/user/{user_id}")
    @Operation(summary = "날짜별 루트 조회", description = """
    - 특정 사용자의 user_id와 조회 날짜(date)를 기준으로 해당 날짜에 생성된 산책로 목록을 반환
    - 날짜는 YYYY-MM-DD 형식으로 전달
    """
    )

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
    @Operation(summary = "루트 완료 처리",
            description = """
    - 특정 산책로(route_id)를 완료 상태로 변경
    - 완료 시각(completed_at)은 서버에서 자동으로 기록
    """
    )
    public ResponseEntity<RouteResponse> complete(
            @PathVariable("route_id") Long route_id,
            @Valid @RequestBody CompleteRouteRequest req) {
        RouteResponse res = routeService.complete(route_id);
        return ResponseEntity.ok(res);
    }

     //루트 내 상점 상세 정보 조회
    @GetMapping("/{route_id}/store/{order_in_route}")
    @Operation(
            summary = "루트 내 상점 상세 조회",
            description = """
    - 특정 루트(route_id) 내에서 지정된 순서(order_in_route)에 해당하는 상점 정보를 반환
    """
    )

    public ResponseEntity<StoreInRouteResponse> storeDetail(
            @PathVariable("route_id") Long routeId,
            @PathVariable("order_in_route") Integer storeId) {

        StoreInRouteResponse res = routeService.getStoreDetail(routeId, storeId);
        return ResponseEntity.ok(res);
    }

}

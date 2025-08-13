package com.codingmyeonga.localstep.routes.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
//루트를 생성하거나 조회했을 때, 서버가 보내주는 루트 전체 정보
@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class RouteResponse {
    private Long route_id;                  // 루트 ID
    private Long user_id;                   // 사용자 ID
    private LocalDateTime created_at;       // 루트 생성 시각
    private Integer goal_steps;              // 목표 걸음 수
    private Double user_lat;                 // 루트 시작 위도
    private Double user_lng;                 // 루트 시작 경도
    private Boolean is_completed;           // 완료 여부
    private LocalDateTime completed_at;     // 완료 시각
    private List<StoreInRouteResponse> stores; // 포함된 상점 목록
}

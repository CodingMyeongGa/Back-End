package com.codingmyeonga.localstep.routes.dto;

import lombok.*;
//루트 안에 포함된 상점 하나의 정보를 표현하는 DTO -- 추가구현 필요
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StoreInRouteResponse {
    private Long route_store_id; // 루트-상점 연결 ID
    private Long store_id;       // 상점 ID
    private Integer order_in_route; // 경로 내 순서
    private String store_name;   // 상점 이름
    private String store_address;// 상점 주소
    private Double store_lat;    // 상점 위도
    private Double store_lng;    // 상점 경도
    private String store_url;    // 상점 상세 페이지 URL
}

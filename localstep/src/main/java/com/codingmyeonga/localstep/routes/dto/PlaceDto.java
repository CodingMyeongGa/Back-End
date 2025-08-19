package com.codingmyeonga.localstep.routes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaceDto {
    private final String store_name;
    private final double store_lat;
    private final double store_lng;
    private final String store_address; // 남가좌동 필터에 사용
}

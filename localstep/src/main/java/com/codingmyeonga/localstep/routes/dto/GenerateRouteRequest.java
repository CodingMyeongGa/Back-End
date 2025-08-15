package com.codingmyeonga.localstep.routes.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
//산책 루트를 생성할 때, 클라이언트가 보내는 데이터 구조
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GenerateRouteRequest {
    @NotNull private Long user_id;
    @NotNull private Double user_lat;
    @NotNull private Double user_lng;
    private Integer goal_steps;
}

package com.codingmyeonga.localstep.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreVisitTestRequestDto {

    @NotNull(message = "사용자 ID는 필수입니다.")
    @JsonProperty("user_id")
    private Long userId;

    @NotNull(message = "루트 ID는 필수입니다.")
    @JsonProperty("route_id")
    private Long routeId;

    @NotNull(message = "상점 ID는 필수입니다.")
    @JsonProperty("store_id")
    private Long storeId;

    @NotNull(message = "사용자 위도는 필수입니다.")
    @JsonProperty("user_lat")
    private BigDecimal userLat;

    @NotNull(message = "사용자 경도는 필수입니다.")
    @JsonProperty("user_lng")
    private BigDecimal userLng;
}

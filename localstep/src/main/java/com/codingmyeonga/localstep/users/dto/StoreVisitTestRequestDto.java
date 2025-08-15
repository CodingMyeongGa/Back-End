package com.codingmyeonga.localstep.users.dto;

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
    private Integer userId;

    @NotNull(message = "루트 ID는 필수입니다.")
    private Integer routeId;

    @NotNull(message = "상점 ID는 필수입니다.")
    private Integer storeId;

    @NotNull(message = "사용자 위도는 필수입니다.")
    private BigDecimal userLat;

    @NotNull(message = "사용자 경도는 필수입니다.")
    private BigDecimal userLng;
}

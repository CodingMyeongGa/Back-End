package com.codingmyeonga.localstep.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequestDto {

    @NotNull(message = "사용자 ID는 필수입니다.")
    @JsonProperty("user_id")
    private Long userId;

    @NotNull(message = "루트 ID는 필수입니다.")
    @JsonProperty("route_id")
    private Long routeId;

    @NotNull(message = "위도는 필수입니다.")
    @JsonProperty("latitude")
    private BigDecimal latitude;

    @NotNull(message = "경도는 필수입니다.")
    @JsonProperty("longitude")
    private BigDecimal longitude;

    @NotNull(message = "측정 시각은 필수입니다.")
    @JsonProperty("timestamp")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}

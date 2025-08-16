package com.codingmyeonga.localstep.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequestDto {

    @NotNull(message = "사용자 ID는 필수입니다.")
    @JsonProperty("user_id")
    private Integer userId;

    @NotNull(message = "위도는 필수입니다.")
    @JsonProperty("latitude")
    private BigDecimal latitude;

    @NotNull(message = "경도는 필수입니다.")
    @JsonProperty("longitude")
    private BigDecimal longitude;

    @NotNull(message = "측정 시각은 필수입니다.")
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
}

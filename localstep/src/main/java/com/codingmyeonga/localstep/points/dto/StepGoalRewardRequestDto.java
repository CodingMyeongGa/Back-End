package com.codingmyeonga.localstep.points.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StepGoalRewardRequestDto {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Integer userId;

    @NotNull(message = "걸음 수 기록 날짜는 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}

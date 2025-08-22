package com.codingmyeonga.localstep.routes.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

//루트를 완료 처리할 때 클라이언트가 보내는 최소 정보
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CompleteRouteRequest {
    @NotNull private Long user_id;
    private LocalDateTime completed_at; //안들어오면 now로 사용
}

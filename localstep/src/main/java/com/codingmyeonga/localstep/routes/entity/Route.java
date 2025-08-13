package com.codingmyeonga.localstep.routes.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
//
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    //루트 고유 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //사용자 ID
    private Long userId;
    //사용자 위도, 경도
    private Double userLat;
    private Double userLng;
    //목표 걸음 수
    private Integer goal_steps;
    //루트 달성 여부 (초기:false)
    private Boolean completed = false;
    //루트 생성 시각
    private LocalDateTime createdAt;
    //루트 완료 시각
    private LocalDateTime completedAt;
}

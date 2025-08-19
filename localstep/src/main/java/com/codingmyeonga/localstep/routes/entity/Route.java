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
    //생성자로 초기 설정
    public Route(Long userId, Double userLat, Double userLng, Integer goalSteps) {
        this.userId = userId;
        this.userLat = userLat;
        this.userLng = userLng;
        this.goal_steps = goalSteps;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
    }
    //루트 완료 처리 메서드
    public void complete(){
        this.completed = true;
        this.completedAt = LocalDateTime.now();


    }

}

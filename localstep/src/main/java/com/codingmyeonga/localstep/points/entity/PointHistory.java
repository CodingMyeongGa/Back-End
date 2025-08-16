package com.codingmyeonga.localstep.points.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.codingmyeonga.localstep.auth.entity.User;
import com.codingmyeonga.localstep.users.entity.StoreVisit;
import com.codingmyeonga.localstep.users.entity.Quest;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_history")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Integer pointId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 30)
    private PointReason reason;

    @Column(name = "points", nullable = false)
    private Integer points;

    @Column(name = "related_visit_id")
    private Integer relatedVisitId;

    @Column(name = "related_quest_id")
    private Integer relatedQuestId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 연관관계 매핑 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_visit_id", insertable = false, updatable = false)
    private StoreVisit relatedVisit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_quest_id", insertable = false, updatable = false)
    private Quest relatedQuest;

    public enum PointReason {
        STORE_VISIT,
        STEP_GOAL
    }
}

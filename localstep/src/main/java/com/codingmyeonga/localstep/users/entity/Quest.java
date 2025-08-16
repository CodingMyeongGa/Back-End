package com.codingmyeonga.localstep.users.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.codingmyeonga.localstep.auth.entity.User;
import com.codingmyeonga.localstep.steps.entity.StepsEntity.Goal;

import java.time.LocalDateTime;

@Entity
@Table(name = "quest")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quest_id")
    private Integer questId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "quest_type", nullable = false, length = 50)
    private QuestType questType;

    @Column(name = "goal_id")
    private Integer goalId;

    @Column(name = "target_store_id")
    private Integer targetStoreId;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "reward_points", nullable = false)
    private Integer rewardPoints;

    // 연관관계 매핑 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", insertable = false, updatable = false)
    private Goal goalSteps;

    public enum QuestType {
        STORE_VISIT,
        STEP_GOAL
    }
}

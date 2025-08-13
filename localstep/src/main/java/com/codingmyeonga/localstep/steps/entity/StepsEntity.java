package com.codingmyeonga.localstep.steps.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;

public class StepsEntity {

    @Entity
    @Table(name = "step_records")
    public static class StepRecord {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "record_id")
        private Long recordId;

        @Column(name = "user_id", nullable = false)
        private Long userId;

        @Column(name = "date", nullable = false)
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;

        @Column(name = "current_steps", nullable = false)
        private Integer currentSteps;

        public Long getRecordId() { return recordId; }
        public void setRecordId(Long recordId) { this.recordId = recordId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public Integer getCurrentSteps() { return currentSteps; }
        public void setCurrentSteps(Integer currentSteps) { this.currentSteps = currentSteps; }
    }

    @Entity
    @Table(name = "goals")
    public static class Goal {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "goal_id")
        private Long goalId;

        @Column(name = "user_id", nullable = false)
        private Long userId;

        @Column(name = "goal_steps", nullable = false)
        private Integer goalSteps;

        @Column(name = "set_date", nullable = false)
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate setDate;

        public Long getGoalId() { return goalId; }
        public void setGoalId(Long goalId) { this.goalId = goalId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Integer getGoalSteps() { return goalSteps; }
        public void setGoalSteps(Integer goalSteps) { this.goalSteps = goalSteps; }
        public LocalDate getSetDate() { return setDate; }
        public void setSetDate(LocalDate setDate) { this.setDate = setDate; }
    }
}

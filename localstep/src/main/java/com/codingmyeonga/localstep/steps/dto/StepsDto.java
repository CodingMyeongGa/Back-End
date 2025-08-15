package com.codingmyeonga.localstep.steps.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;

public class StepsDto {

    public static class PostStepsRequest {
        private Long user_id;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;
        private Integer current_steps;

        public Long getUser_id() { return user_id; }
        public void setUser_id(Long user_id) { this.user_id = user_id; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public Integer getCurrent_steps() { return current_steps; }
        public void setCurrent_steps(Integer current_steps) { this.current_steps = current_steps; }
    }

    public static class PutStepsRequest {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;
        private Integer current_steps;

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public Integer getCurrent_steps() { return current_steps; }
        public void setCurrent_steps(Integer current_steps) { this.current_steps = current_steps; }
    }

    public static class StepsResponse {
        private Long record_id;
        private Long user_id;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;
        private Integer current_steps;

        public Long getRecord_id() { return record_id; }
        public void setRecord_id(Long record_id) { this.record_id = record_id; }
        public Long getUser_id() { return user_id; }
        public void setUser_id(Long user_id) { this.user_id = user_id; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public Integer getCurrent_steps() { return current_steps; }
        public void setCurrent_steps(Integer current_steps) { this.current_steps = current_steps; }
    }

    public static class PostGoalRequest {
        private Long user_id;
        private Integer goal_steps;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate set_date;

        public Long getUser_id() { return user_id; }
        public void setUser_id(Long user_id) { this.user_id = user_id; }
        public Integer getGoal_steps() { return goal_steps; }
        public void setGoal_steps(Integer goal_steps) { this.goal_steps = goal_steps; }
        public LocalDate getSet_date() { return set_date; }
        public void setSet_date(LocalDate set_date) { this.set_date = set_date; }
    }

    public static class GoalResponse {
        private Long goal_id;
        private Long user_id;
        private Integer goal_steps;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate set_date;

        public Long getGoal_id() { return goal_id; }
        public void setGoal_id(Long goal_id) { this.goal_id = goal_id; }
        public Long getUser_id() { return user_id; }
        public void setUser_id(Long user_id) { this.user_id = user_id; }
        public Integer getGoal_steps() { return goal_steps; }
        public void setGoal_steps(Integer goal_steps) { this.goal_steps = goal_steps; }
        public LocalDate getSet_date() { return set_date; }
        public void setSet_date(LocalDate set_date) { this.set_date = set_date; }
    }

    public static class HistoryItem {
        private String date;
        private Integer current_steps;
        private Integer goal_steps;

        public HistoryItem() {}
        public HistoryItem(String date, Integer current_steps, Integer goal_steps) {
            this.date = date;
            this.current_steps = current_steps;
            this.goal_steps = goal_steps;
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public Integer getCurrent_steps() { return current_steps; }
        public void setCurrent_steps(Integer current_steps) { this.current_steps = current_steps; }
        public Integer getGoal_steps() { return goal_steps; }
        public void setGoal_steps(Integer goal_steps) { this.goal_steps = goal_steps; }
    }

    public static class HistoryResponse {
        private List<HistoryItem> items;

        public HistoryResponse() {}
        public HistoryResponse(List<HistoryItem> items) { this.items = items; }

        public List<HistoryItem> getItems() { return items; }
        public void setItems(List<HistoryItem> items) { this.items = items; }
    }
}

package com.codingmyeonga.localstep.steps.service;

import com.codingmyeonga.localstep.steps.dto.StepsDto.*;
import com.codingmyeonga.localstep.steps.entity.StepsEntity.Goal;
import com.codingmyeonga.localstep.steps.entity.StepsEntity.StepRecord;
import com.codingmyeonga.localstep.steps.repository.GoalRepository;
import com.codingmyeonga.localstep.steps.repository.StepRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StepsService {

    private final StepRecordRepository stepRecordRepository;
    private final GoalRepository goalRepository;

    public StepsService(StepRecordRepository stepRecordRepository, GoalRepository goalRepository) {
        this.stepRecordRepository = stepRecordRepository;
        this.goalRepository = goalRepository;
    }

    public StepsResponse createSteps(PostStepsRequest req) {
        StepRecord record = stepRecordRepository.findByUserIdAndDate(req.getUser_id(), req.getDate())
                .orElseGet(StepRecord::new);
        record.setUserId(req.getUser_id());
        record.setDate(req.getDate());
        record.setCurrentSteps(req.getCurrent_steps());
        StepRecord saved = stepRecordRepository.save(record);
        StepsResponse res = new StepsResponse();
        res.setRecord_id(saved.getRecordId());
        res.setUser_id(saved.getUserId());
        res.setDate(saved.getDate());
        res.setCurrent_steps(saved.getCurrentSteps());
        return res;
    }

    public StepsResponse updateSteps(Long userId, PutStepsRequest req) {
        StepRecord record = stepRecordRepository.findByUserIdAndDate(userId, req.getDate())
                .orElseGet(StepRecord::new);
        record.setUserId(userId);
        record.setDate(req.getDate());
        record.setCurrentSteps(req.getCurrent_steps());
        StepRecord saved = stepRecordRepository.save(record);
        StepsResponse res = new StepsResponse();
        res.setRecord_id(saved.getRecordId());
        res.setUser_id(saved.getUserId());
        res.setDate(saved.getDate());
        res.setCurrent_steps(saved.getCurrentSteps());
        return res;
    }

    public GoalResponse setGoal(PostGoalRequest req) {
        Goal goal = new Goal();
        goal.setUserId(req.getUser_id());
        goal.setGoalSteps(req.getGoal_steps());
        goal.setSetDate(req.getSet_date());
        Goal saved = goalRepository.save(goal);
        GoalResponse res = new GoalResponse();
        res.setGoal_id(saved.getGoalId());
        res.setUser_id(saved.getUserId());
        res.setGoal_steps(saved.getGoalSteps());
        res.setSet_date(saved.getSetDate());
        return res;
    }

    @Transactional(readOnly = true)
    public GoalResponse getGoal(Long userId) {
        Goal goal = goalRepository.findTopByUserIdOrderBySetDateDesc(userId).orElse(null);
        GoalResponse res = new GoalResponse();
        if (goal != null) {
            res.setGoal_id(goal.getGoalId());
            res.setUser_id(goal.getUserId());
            res.setGoal_steps(goal.getGoalSteps());
            res.setSet_date(goal.getSetDate());
        }
        return res;
    }

    @Transactional(readOnly = true)
    public HistoryResponse getHistory(Long userId, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<StepRecord> records = stepRecordRepository.findAllByUserIdAndDateBetweenOrderByDateAsc(userId, start, end);
        List<HistoryItem> items = new ArrayList<>();
        for (StepRecord r : records) {
            Integer goalSteps = goalRepository.findTopByUserIdAndSetDateLessThanEqualOrderBySetDateDesc(userId, r.getDate())
                    .map(Goal::getGoalSteps)
                    .orElse(null);
            items.add(new HistoryItem(r.getDate().toString(), r.getCurrentSteps(), goalSteps));
        }
        return new HistoryResponse(items);
    }
}

package com.codingmyeonga.localstep.steps.controller;

import com.codingmyeonga.localstep.steps.dto.StepsDto.*;
import com.codingmyeonga.localstep.steps.service.StepsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/steps")
public class StepsController {

    private final StepsService stepsService;

    public StepsController(StepsService stepsService) {
        this.stepsService = stepsService;
    }

    @PostMapping("/data")
    public ResponseEntity<StepsResponse> postSteps(@RequestBody PostStepsRequest request) {
        return ResponseEntity.ok(stepsService.createSteps(request));
    }

    @PutMapping("/data/{user_id}")
    public ResponseEntity<StepsResponse> putSteps(@PathVariable("user_id") Long userId,
                                                  @RequestBody PutStepsRequest request) {
        return ResponseEntity.ok(stepsService.updateSteps(userId, request));
    }

    @PostMapping("/goal")
    public ResponseEntity<GoalResponse> postGoal(@RequestBody PostGoalRequest request) {
        return ResponseEntity.ok(stepsService.setGoal(request));
    }

    @GetMapping("/goal/{user_id}")
    public ResponseEntity<GoalResponse> getGoal(@PathVariable("user_id") Long userId) {
        return ResponseEntity.ok(stepsService.getGoal(userId));
    }
    
    @GetMapping("/goal")
    public ResponseEntity<GoalResponse> getGoalDefault(@RequestParam("user_id") Long userId) {
        return ResponseEntity.ok(stepsService.getGoal(userId));
    }

    @GetMapping("/history/{user_id}")
    public ResponseEntity<HistoryResponse> getHistory(@PathVariable("user_id") Long userId,
                                                      @RequestParam("startDate") String startDate,
                                                      @RequestParam("endDate") String endDate) {
        return ResponseEntity.ok(stepsService.getHistory(userId, startDate, endDate));
    }
}

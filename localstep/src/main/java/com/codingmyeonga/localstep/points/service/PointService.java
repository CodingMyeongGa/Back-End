package com.codingmyeonga.localstep.points.service;

import com.codingmyeonga.localstep.points.dto.PointBalanceResponseDto;
import com.codingmyeonga.localstep.points.dto.PointHistoryResponseDto;
import com.codingmyeonga.localstep.points.dto.StepGoalRewardRequestDto;
import com.codingmyeonga.localstep.points.dto.StepGoalRewardResponseDto;
import com.codingmyeonga.localstep.points.entity.PointHistory;
import com.codingmyeonga.localstep.points.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public void addPoints(Integer userId, Integer points, PointHistory.PointReason reason, 
                         Integer relatedVisitId, Integer relatedQuestId) {
        
        PointHistory pointHistory = PointHistory.builder()
                .userId(userId)
                .points(points)
                .reason(reason)
                .relatedVisitId(relatedVisitId)
                .relatedQuestId(relatedQuestId)
                .createdAt(LocalDateTime.now())
                .build();
        
        pointHistoryRepository.save(pointHistory);
    }
    
    /**
     * 사용자의 포인트 잔액을 조회합니다.
     * @param userId 사용자 ID
     * @return 포인트 잔액 정보
     */
    public PointBalanceResponseDto getPointBalance(Integer userId) {
        Integer totalPoints = pointHistoryRepository.calculateTotalPointsByUserId(userId);
        
        return new PointBalanceResponseDto(userId, totalPoints);
    }
    
    /**
     * 사용자의 포인트 히스토리를 조회합니다.
     * @param userId 사용자 ID
     * @param startDate 시작 날짜 (선택사항)
     * @param endDate 종료 날짜 (선택사항)
     * @return 포인트 히스토리 목록
     */
    public List<PointHistoryResponseDto> getPointHistory(Integer userId, LocalDate startDate, LocalDate endDate) {
        List<PointHistory> histories;
        
        if (startDate != null && endDate != null) {
            // 날짜 범위가 지정된 경우
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            histories = pointHistoryRepository.findByUserIdAndDateRangeOrderByCreatedAtDesc(userId, startDateTime, endDateTime);
        } else {
            // 날짜 범위가 지정되지 않은 경우 (전체 조회)
            histories = pointHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        
        return histories.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * PointHistory 엔티티를 PointHistoryResponseDto로 변환합니다.
     */
    private PointHistoryResponseDto convertToResponseDto(PointHistory history) {
        return PointHistoryResponseDto.builder()
                .pointId(history.getPointId())
                .reason(getReasonDisplayName(history.getReason()))
                .points(history.getPoints())
                .createdAt(history.getCreatedAt())
                .build();
    }
    
    /**
     * 포인트 지급/차감 사유를 사용자 친화적인 텍스트로 변환합니다.
     */
    private String getReasonDisplayName(PointHistory.PointReason reason) {
        switch (reason) {
            case STORE_VISIT:
                return "STORE_VISIT";
            case STEP_GOAL:
                return "STEP_GOAL";
            default:
                return "else";
        }
    }
    
    /**
     * 걸음 수 목표 달성 포인트를 지급합니다.
     * @param requestDto 걸음 목표 달성 요청
     * @return 걸음 목표 달성 결과
     */
    @Transactional
    public StepGoalRewardResponseDto rewardStepGoal(StepGoalRewardRequestDto requestDto) {
        
        // 1. 중복 지급 체크 (하루에 한 번만)
        if (isStepGoalRewardAlreadyGiven(requestDto.getUserId(), requestDto.getDate())) {
            return StepGoalRewardResponseDto.builder()
                    .goalId(1) // Mock goal ID
                    .goalSteps(getUserStepGoal(requestDto.getUserId()))
                    .currentSteps(getUserCurrentSteps(requestDto.getUserId(), requestDto.getDate()))
                    .goalAchieved(true)
                    .pointsAwarded(0)
                    .message("오늘 이미 걸음 목표 달성 포인트를 받았습니다.")
                    .build();
        }
        
        // 2. 사용자의 걸음 수와 목표 조회 (Mock)
        Integer currentSteps = getUserCurrentSteps(requestDto.getUserId(), requestDto.getDate());
        Integer goalSteps = getUserStepGoal(requestDto.getUserId());
        
        // 3. 목표 달성 여부 확인
        boolean goalAchieved = currentSteps >= goalSteps;
        
        if (!goalAchieved) {
            return StepGoalRewardResponseDto.builder()
                    .goalId(1) // Mock goal ID
                    .goalSteps(goalSteps)
                    .currentSteps(currentSteps)
                    .goalAchieved(false)
                    .pointsAwarded(0)
                    .message("아직 걸음 목표를 달성하지 못했습니다.")
                    .build();
        }
        
        // 4. 포인트 지급
        Integer pointsToAward = STEP_GOAL_POINTS;
        addPoints(
            requestDto.getUserId(), 
            pointsToAward, 
            PointHistory.PointReason.STEP_GOAL, 
            null, 
            1 // Mock quest ID
        );
        
        return StepGoalRewardResponseDto.builder()
                .goalId(1) // Mock goal ID
                .goalSteps(goalSteps)
                .currentSteps(currentSteps)
                .goalAchieved(true)
                .pointsAwarded(pointsToAward)
                .message("목표를 달성하여 " + pointsToAward + "포인트가 지급되었습니다.")
                .build();
    }
    
    /**
     * 특정 날짜에 걸음 목표 달성 포인트가 이미 지급되었는지 확인합니다.
     */
    private boolean isStepGoalRewardAlreadyGiven(Integer userId, LocalDate date) {
        return pointHistoryRepository.existsStepGoalRewardByUserIdAndDate(userId, date);
    }
    
    /**
     * 사용자의 목표 걸음 수를 조회합니다. (Mock)
     * TODO: 나중에 StepsRepository에서 실제 데이터로 교체
     */
    private Integer getUserStepGoal(Integer userId) {
        // Mock: 모든 사용자의 목표를 5,000걸음으로 설정
        return 5000;
    }
    
    /**
     * 사용자의 현재 걸음 수를 조회합니다. (Mock)
     * TODO: 나중에 StepsRepository에서 실제 데이터로 교체
     */
    private Integer getUserCurrentSteps(Integer userId, LocalDate date) {
        // Mock: 모든 사용자의 걸음 수를 6,000걸음으로 설정
        return 6000;
    }
    
    // 걸음 목표 달성 시 지급할 포인트
    private static final Integer STEP_GOAL_POINTS = 500;
}

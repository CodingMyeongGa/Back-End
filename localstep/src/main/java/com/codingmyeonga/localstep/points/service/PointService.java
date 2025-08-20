package com.codingmyeonga.localstep.points.service;

import com.codingmyeonga.localstep.points.dto.PointBalanceResponseDto;
import com.codingmyeonga.localstep.points.dto.PointHistoryResponseDto;
import com.codingmyeonga.localstep.points.dto.StepGoalRewardRequestDto;
import com.codingmyeonga.localstep.points.dto.StepGoalRewardResponseDto;
import com.codingmyeonga.localstep.points.entity.PointHistory;
import com.codingmyeonga.localstep.points.repository.PointHistoryRepository;
import com.codingmyeonga.localstep.steps.entity.StepsEntity.Goal;
import com.codingmyeonga.localstep.steps.entity.StepsEntity.StepRecord;
import com.codingmyeonga.localstep.steps.repository.GoalRepository;
import com.codingmyeonga.localstep.users.entity.Quest;
import com.codingmyeonga.localstep.users.repository.QuestRepository;
import com.codingmyeonga.localstep.auth.repository.UserRepository;
import com.codingmyeonga.localstep.auth.exception.ApiException;
import org.springframework.http.HttpStatus;
import com.codingmyeonga.localstep.steps.repository.StepRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final GoalRepository goalRepository;
    private final StepRecordRepository stepRecordRepository;
    private final QuestRepository questRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addPoints(Long userId, Integer points, PointHistory.PointReason reason, 
                         Long relatedVisitId, Long relatedQuestId, LocalDate rewardDate) {
        
        PointHistory pointHistory = PointHistory.builder()
                .userId(userId)
                .points(points)
                .reason(reason)
                .relatedVisitId(relatedVisitId)
                .relatedQuestId(relatedQuestId)
                .createdAt(LocalDateTime.now())
                .rewardDate(rewardDate)
                .build();
        
        pointHistoryRepository.save(pointHistory);
    }
    
    /**
     * 사용자의 포인트 잔액을 조회합니다.
     * @param userId 사용자 ID
     * @return 포인트 잔액 정보
     */
    public PointBalanceResponseDto getPointBalance(Long userId) {
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 사용자입니다.");
        }
        
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
    public List<PointHistoryResponseDto> getPointHistory(Long userId, LocalDate startDate, LocalDate endDate) {
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 사용자입니다.");
        }
        
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
        
        log.info("걸음 목표 달성 요청: userId={}, date={}", requestDto.getUserId(), requestDto.getDate());
        
        // 1. 중복 지급 체크 (하루에 한 번만)
        boolean alreadyGiven = isStepGoalRewardAlreadyGiven(requestDto.getUserId(), requestDto.getDate());
        log.info("중복 지급 체크 결과: alreadyGiven={}", alreadyGiven);
        
        if (alreadyGiven) {
            return StepGoalRewardResponseDto.builder()
                    .goalId(getUserGoalId(requestDto.getUserId()))
                    .goalSteps(getUserStepGoal(requestDto.getUserId()))
                    .currentSteps(getUserCurrentSteps(requestDto.getUserId(), requestDto.getDate()))
                    .goalAchieved(true)
                    .pointsAwarded(0)
                    .message("오늘 이미 걸음 목표 달성 포인트를 받았습니다.")
                    .build();
        }
        
        // 2. 사용자의 걸음 수와 목표 조회
        Integer currentSteps = getUserCurrentSteps(requestDto.getUserId(), requestDto.getDate());
        Integer goalSteps = getUserStepGoal(requestDto.getUserId());
        
        // 목표가 설정되지 않은 경우 처리
        if (goalSteps == 0) {
            return StepGoalRewardResponseDto.builder()
                    .goalId(null)
                    .goalSteps(0)
                    .currentSteps(currentSteps)
                    .goalAchieved(false)
                    .pointsAwarded(0)
                    .message("걸음 목표가 설정되지 않았습니다.")
                    .build();
        }
        
        // 3. 목표 달성 여부 확인
        boolean goalAchieved = currentSteps >= goalSteps;
        
        if (!goalAchieved) {
            return StepGoalRewardResponseDto.builder()
                    .goalId(getUserGoalId(requestDto.getUserId()))
                    .goalSteps(goalSteps)
                    .currentSteps(currentSteps)
                    .goalAchieved(false)
                    .pointsAwarded(0)
                    .message("아직 걸음 목표를 달성하지 못했습니다.")
                    .build();
        }
        
        // 4. 포인트 지급 (중복 방지)
        Integer pointsToAward = calculateStepGoalPoints(goalSteps);
        try {
            // 4-1. 사용자 존재 여부 확인 (FK 에러 방지)
            if (!userRepository.existsById(requestDto.getUserId())) {
                throw new ApiException(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 사용자입니다.");
            }

            // 4-2. 퀘스트 생성(걸음 목표 달성)
            Quest quest = Quest.builder()
                    .userId(requestDto.getUserId())
                    .questType(Quest.QuestType.STEP_GOAL)
                    .goalId(getUserGoalId(requestDto.getUserId()))
                    .rewardPoints(pointsToAward)
                    .build();
            Quest savedQuest = questRepository.save(quest);

            // 4-3. 포인트 지급 (퀘스트 ID 연동)
            addPoints(
                requestDto.getUserId(), 
                pointsToAward, 
                PointHistory.PointReason.STEP_GOAL, 
                null, 
                savedQuest.getQuestId(),
                requestDto.getDate()
            );
            
            return StepGoalRewardResponseDto.builder()
                    .goalId(getUserGoalId(requestDto.getUserId()))
                    .goalSteps(goalSteps)
                    .currentSteps(currentSteps)
                    .goalAchieved(true)
                    .pointsAwarded(pointsToAward)
                    .message("목표를 달성하여 " + pointsToAward + "포인트가 지급되었습니다.")
                    .build();
        } catch (DataIntegrityViolationException e) {
            // 중복 지급 시도 시 이미 지급된 것으로 처리
            return StepGoalRewardResponseDto.builder()
                    .goalId(getUserGoalId(requestDto.getUserId()))
                    .goalSteps(goalSteps)
                    .currentSteps(currentSteps)
                    .goalAchieved(true)
                    .pointsAwarded(0)
                    .message("오늘 이미 걸음 목표 달성 포인트를 받았습니다.")
                    .build();
        }
    }
    
    /**
     * 특정 날짜에 걸음 목표 달성 포인트가 이미 지급되었는지 확인합니다.
     */
    private boolean isStepGoalRewardAlreadyGiven(Long userId, LocalDate date) {
        return pointHistoryRepository.existsStepGoalRewardByUserIdAndDate(userId, date);
    }
    
    /**
     * 사용자의 목표 걸음 수를 조회합니다.
     */
    private Integer getUserStepGoal(Long userId) {
        Optional<Goal> goal = goalRepository.findTopByUserIdOrderBySetDateDesc(userId);
        return goal.map(Goal::getGoalSteps).orElse(0);
    }
    
    /**
     * 사용자의 현재 걸음 수를 조회합니다.
     */
    private Integer getUserCurrentSteps(Long userId, LocalDate date) {
        Optional<StepRecord> record = stepRecordRepository.findByUserIdAndDate(userId, date);
        return record.map(StepRecord::getCurrentSteps).orElse(0);
    }
    
    /**
     * 사용자의 목표 ID를 조회합니다.
     */
    private Long getUserGoalId(Long userId) {
        Optional<Goal> goal = goalRepository.findTopByUserIdOrderBySetDateDesc(userId);
        return goal.map(Goal::getGoalId).orElse(null);
    }
    
    /**
     * 목표 걸음 수에 따른 포인트를 계산합니다.
     * 목표 걸음 수의 10% (1의자리 버림)
     */
    private Integer calculateStepGoalPoints(Integer goalSteps) {
        return (goalSteps / 10); // 목표 걸음 수의 10% (1의자리 버림)
    }
}

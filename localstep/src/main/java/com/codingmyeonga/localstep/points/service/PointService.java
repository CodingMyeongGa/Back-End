package com.codingmyeonga.localstep.points.service;

import com.codingmyeonga.localstep.points.dto.PointBalanceResponseDto;
import com.codingmyeonga.localstep.points.dto.PointHistoryResponseDto;
import com.codingmyeonga.localstep.points.entity.PointHistory;
import com.codingmyeonga.localstep.points.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
     * @return 포인트 히스토리 목록
     */
    public List<PointHistoryResponseDto> getPointHistory(Integer userId) {
        List<PointHistory> histories = pointHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
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
}

package com.codingmyeonga.localstep.points.service;

import com.codingmyeonga.localstep.points.entity.PointHistory;
import com.codingmyeonga.localstep.points.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
}

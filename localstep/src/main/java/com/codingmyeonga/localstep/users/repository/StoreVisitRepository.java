package com.codingmyeonga.localstep.users.repository;

import com.codingmyeonga.localstep.users.entity.StoreVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StoreVisitRepository extends JpaRepository<StoreVisit, Integer> {
    
    // 중복 방문 체크를 위한 메서드
    boolean existsByUserIdAndStoreId(Integer userId, Integer storeId);
    
    // 날짜 범위로 방문 기록 조회
    @Query("SELECT sv FROM StoreVisit sv WHERE sv.userId = :userId " +
           "AND (:startDate IS NULL OR sv.visitedAt >= :startDate) " +
           "AND (:endDate IS NULL OR sv.visitedAt <= :endDate) " +
           "ORDER BY sv.visitedAt DESC")
    List<StoreVisit> findByUserIdAndDateRange(
        @Param("userId") Integer userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}

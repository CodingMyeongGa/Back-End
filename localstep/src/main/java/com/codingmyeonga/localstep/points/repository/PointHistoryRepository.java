package com.codingmyeonga.localstep.points.repository;

import com.codingmyeonga.localstep.points.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Integer> {
    
    // 사용자의 총 포인트 잔액 계산
    @Query("SELECT COALESCE(SUM(ph.points), 0) FROM PointHistory ph WHERE ph.userId = :userId")
    Integer calculateTotalPointsByUserId(@Param("userId") Integer userId);
    
    // 사용자의 포인트 히스토리 조회 (최신순)
    @Query("SELECT ph FROM PointHistory ph WHERE ph.userId = :userId ORDER BY ph.createdAt DESC")
    List<PointHistory> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);
}

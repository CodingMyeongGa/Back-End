package com.codingmyeonga.localstep.points.repository;

import com.codingmyeonga.localstep.points.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Integer> {
}

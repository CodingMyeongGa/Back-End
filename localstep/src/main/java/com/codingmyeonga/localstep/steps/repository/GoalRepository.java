package com.codingmyeonga.localstep.steps.repository;

import com.codingmyeonga.localstep.steps.entity.StepsEntity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    Optional<Goal> findTopByUserIdOrderBySetDateDesc(Long userId);
    Optional<Goal> findTopByUserIdAndSetDateLessThanEqualOrderBySetDateDesc(Long userId, LocalDate date);
}

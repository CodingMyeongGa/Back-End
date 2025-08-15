package com.codingmyeonga.localstep.steps.repository;

import com.codingmyeonga.localstep.steps.entity.StepsEntity.StepRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StepRecordRepository extends JpaRepository<StepRecord, Long> {
    Optional<StepRecord> findByUserIdAndDate(Long userId, LocalDate date);
    List<StepRecord> findAllByUserIdAndDateBetweenOrderByDateAsc(Long userId, LocalDate start, LocalDate end);
}

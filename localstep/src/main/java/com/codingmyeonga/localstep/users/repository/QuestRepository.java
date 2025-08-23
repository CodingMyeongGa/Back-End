package com.codingmyeonga.localstep.users.repository;

import com.codingmyeonga.localstep.users.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestRepository extends JpaRepository<Quest, Long> {
}



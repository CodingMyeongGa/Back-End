package com.codingmyeonga.localstep.routes.repository;

import com.codingmyeonga.localstep.routes.entity.Route;
import com.codingmyeonga.localstep.routes.entity.RouteStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findAllByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}

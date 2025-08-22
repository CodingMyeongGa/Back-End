package com.codingmyeonga.localstep.routes.repository;

import com.codingmyeonga.localstep.routes.entity.RouteStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface RouteStoreRepository extends JpaRepository<RouteStore, Long> {
    Optional<RouteStore> findByRouteIdAndOrderInRoute(Long routeId, Integer orderInRoute);
    List<RouteStore> findAllByRouteIdOrderByOrderInRouteAsc(Long routeId);
    
    // StoreVisitService에서 사용할 메서드들
    RouteStore findByRouteIdAndStoreId(Long routeId, Long storeId);
    List<RouteStore> findByRouteId(Long routeId);
}

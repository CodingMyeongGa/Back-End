package com.codingmyeonga.localstep.users.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.codingmyeonga.localstep.auth.entity.User;
import com.codingmyeonga.localstep.routes.entity.Route;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "store_visit", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "route_id", "store_id"}, name = "uk_user_route_store")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id")
    private Long visitId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "route_id", nullable = false)
    private Long routeId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "user_latitude", nullable = false, precision = 10, scale = 6)
    private BigDecimal userLatitude;

    @Column(name = "user_longitude", nullable = false, precision = 10, scale = 6)
    private BigDecimal userLongitude;

    @Column(name = "visited_at", nullable = false)
    private LocalDateTime visitedAt;

    @Column(name = "points_awarded", nullable = false)
    private Integer pointsAwarded;

    // 연관관계 매핑 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", insertable = false, updatable = false)
    private Route route;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "store_id", insertable = false, updatable = false)
    // private Store store;
}

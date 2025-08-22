package com.codingmyeonga.localstep.routes.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteStore {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                 // route_store_id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private Long storeId;

    @Column(nullable = false)
    private Integer orderInRoute;

    @Column(nullable = false, length = 200)
    private String storeName;

    @Column(nullable = false, length = 300)
    private String storeAddress;

    @Column(nullable = false)
    private Double storeLat;

    @Column(nullable = false)
    private Double storeLng;

    @Column(length = 500)
    private String storeUrl;
}

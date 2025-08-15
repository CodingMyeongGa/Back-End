package com.codingmyeonga.localstep.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreVisitResponseDto {

    private Integer visitId;
    private Integer storeId;
    private String storeName;
    private LocalDateTime visitedAt;
    private Integer pointsAwarded;
}

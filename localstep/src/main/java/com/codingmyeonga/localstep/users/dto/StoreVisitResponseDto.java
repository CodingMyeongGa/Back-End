package com.codingmyeonga.localstep.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreVisitResponseDto {

    @JsonProperty("visit_id")
    private Long visitId;
    
    @JsonProperty("store_id")
    private Long storeId;
    
    @JsonProperty("store_name")
    private String storeName;
    
    @JsonProperty("visited_at")
    private LocalDateTime visitedAt;
    
    @JsonProperty("points_awarded")
    private Integer pointsAwarded;
}

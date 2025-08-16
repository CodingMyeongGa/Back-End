package com.codingmyeonga.localstep.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationResponseDto {

    @JsonProperty("nearby_store_detected")
    private Boolean nearbyStoreDetected;
    
    @JsonProperty("store_id")
    private Integer storeId;
    
    @JsonProperty("visit_id")
    private Integer visitId;
    
    @JsonProperty("points_awarded")
    private Integer pointsAwarded;
    
    private String message;
}

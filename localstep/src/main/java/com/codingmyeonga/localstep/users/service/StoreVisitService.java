package com.codingmyeonga.localstep.users.service;

import com.codingmyeonga.localstep.points.entity.PointHistory;
import com.codingmyeonga.localstep.points.service.PointService;
import com.codingmyeonga.localstep.users.dto.StoreVisitResponseDto;
import com.codingmyeonga.localstep.users.dto.StoreVisitTestRequestDto;
import com.codingmyeonga.localstep.users.dto.StoreVisitTestResponseDto;
import com.codingmyeonga.localstep.users.entity.StoreVisit;
import com.codingmyeonga.localstep.users.repository.StoreVisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreVisitService {

    private final StoreVisitRepository storeVisitRepository;
    private final PointService pointService;
    
    // 상점 위치 정보 (실제로는 외부 API에서 가져와야 함)
    // 테스트용으로 하드코딩된 상점 위치들
    private static final BigDecimal STORE_1_LAT = new BigDecimal("37.5665");
    private static final BigDecimal STORE_1_LNG = new BigDecimal("126.9780");
    private static final BigDecimal STORE_2_LAT = new BigDecimal("37.5666");
    private static final BigDecimal STORE_2_LNG = new BigDecimal("126.9781");
    
    // 방문 시 지급할 포인트
    private static final Integer VISIT_POINTS = 100;
    
    // 상점 근처 기준 반경 (미터)
    private static final double NEARBY_RADIUS_METERS = 50.0;

    @Transactional
    public StoreVisitTestResponseDto createVisit(StoreVisitTestRequestDto requestDto) {
        
        // 1. 중복 방문 체크
        if (isDuplicateVisit(requestDto.getUserId(), requestDto.getStoreId())) {
            return StoreVisitTestResponseDto.builder()
                    .visitId(null)
                    .pointsAwarded(0)
                    .message("이미 방문한 상점입니다.")
                    .build();
        }
        
        // 2. 상점 위치 가져오기 (실제로는 외부 API 호출)
        StoreLocation storeLocation = getStoreLocation(requestDto.getStoreId());
        if (storeLocation == null) {
            return StoreVisitTestResponseDto.builder()
                    .visitId(null)
                    .pointsAwarded(0)
                    .message("상점 정보를 찾을 수 없습니다.")
                    .build();
        }
        
        // 3. 위치 검증 (50m 반경 내에 있는지)
        if (!isUserNearStore(requestDto.getUserLat(), requestDto.getUserLng(), 
                           storeLocation.latitude, storeLocation.longitude)) {
            return StoreVisitTestResponseDto.builder()
                    .visitId(null)
                    .pointsAwarded(0)
                    .message("상점 근처에 있지 않습니다. (50m 이내로 이동해주세요)")
                    .build();
        }
        
        // 4. 방문 기록 저장
        StoreVisit storeVisit = StoreVisit.builder()
                .userId(requestDto.getUserId())
                .routeId(requestDto.getRouteId())
                .storeId(requestDto.getStoreId())
                .userLatitude(requestDto.getUserLat())
                .userLongitude(requestDto.getUserLng())
                .visitedAt(LocalDateTime.now())
                .pointsAwarded(VISIT_POINTS)
                .build();
        
        StoreVisit savedVisit = storeVisitRepository.save(storeVisit);
        
        // 5. 포인트 지급
        pointService.addPoints(
            requestDto.getUserId(), 
            VISIT_POINTS, 
            PointHistory.PointReason.STORE_VISIT, 
            savedVisit.getVisitId(), 
            null
        );
        
        return StoreVisitTestResponseDto.builder()
                .visitId(savedVisit.getVisitId())
                .pointsAwarded(VISIT_POINTS)
                .message("방문 기록이 저장되고 포인트가 지급되었습니다.")
                .build();
    }
    
    private boolean isDuplicateVisit(Integer userId, Integer storeId) {
        return storeVisitRepository.existsByUserIdAndStoreId(userId, storeId);
    }
    
    private StoreLocation getStoreLocation(Integer storeId) {
        // 실제로는 외부 API에서 상점 정보를 가져와야 함
        // 테스트용으로 하드코딩된 위치 반환
        switch (storeId) {
            case 1:
                return new StoreLocation(STORE_1_LAT, STORE_1_LNG);
            case 2:
                return new StoreLocation(STORE_2_LAT, STORE_2_LNG);
            default:
                return null;
        }
    }
    
    private boolean isUserNearStore(BigDecimal userLat, BigDecimal userLng, 
                                   BigDecimal storeLat, BigDecimal storeLng) {
        // Haversine 공식을 사용하여 두 지점 간의 거리 계산
        double distance = calculateDistance(
            userLat.doubleValue(), userLng.doubleValue(),
            storeLat.doubleValue(), storeLng.doubleValue()
        );
        
        return distance <= NEARBY_RADIUS_METERS;
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // 지구의 반지름 (미터)
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // 미터 단위
    }
    
    /**
     * 사용자의 방문 기록을 조회합니다.
     * @param userId 사용자 ID
     * @param startDate 시작 날짜 (선택사항)
     * @param endDate 종료 날짜 (선택사항)
     * @return 방문 기록 목록
     */
    public List<StoreVisitResponseDto> getUserVisits(Integer userId, LocalDate startDate, LocalDate endDate) {
        // 날짜 범위 설정
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        
        if (startDate != null) {
            startDateTime = startDate.atStartOfDay();
        }
        
        if (endDate != null) {
            endDateTime = endDate.atTime(LocalTime.MAX); // 해당 날짜의 마지막 시간
        }
        
        // 방문 기록 조회
        List<StoreVisit> visits = storeVisitRepository.findByUserIdAndDateRange(userId, startDateTime, endDateTime);
        
        // DTO로 변환
        return visits.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * StoreVisit 엔티티를 StoreVisitResponseDto로 변환합니다.
     */
    private StoreVisitResponseDto convertToResponseDto(StoreVisit visit) {
        return StoreVisitResponseDto.builder()
                .visitId(visit.getVisitId())
                .storeId(visit.getStoreId())
                .storeName(getStoreName(visit.getStoreId())) // 상점 이름 가져오기
                .visitedAt(visit.getVisitedAt())
                .pointsAwarded(visit.getPointsAwarded())
                .build();
    }
    
    /**
     * 상점 ID로 상점 이름을 가져옵니다.
     * 실제로는 외부 API에서 가져와야 함
     */
    private String getStoreName(Integer storeId) {
        // 테스트용으로 하드코딩된 상점 이름
        switch (storeId) {
            case 1:
                return "테스트 상점 1";
            case 2:
                return "테스트 상점 2";
            default:
                return "알 수 없는 상점";
        }
    }
    
    // 상점 위치 정보를 담는 내부 클래스
    private static class StoreLocation {
        private final BigDecimal latitude;
        private final BigDecimal longitude;
        
        public StoreLocation(BigDecimal latitude, BigDecimal longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}

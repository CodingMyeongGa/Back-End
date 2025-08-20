package com.codingmyeonga.localstep.users.service;

import com.codingmyeonga.localstep.points.entity.PointHistory;
import com.codingmyeonga.localstep.points.service.PointService;
import com.codingmyeonga.localstep.users.dto.LocationRequestDto;
import com.codingmyeonga.localstep.users.dto.LocationResponseDto;
import com.codingmyeonga.localstep.users.dto.StoreVisitResponseDto;
import com.codingmyeonga.localstep.users.dto.StoreVisitTestRequestDto;
import com.codingmyeonga.localstep.users.dto.StoreVisitTestResponseDto;
import com.codingmyeonga.localstep.users.entity.StoreVisit;
import com.codingmyeonga.localstep.users.entity.Quest;
import com.codingmyeonga.localstep.users.repository.StoreVisitRepository;
import com.codingmyeonga.localstep.users.repository.QuestRepository;
import com.codingmyeonga.localstep.auth.repository.UserRepository;
import com.codingmyeonga.localstep.routes.repository.RouteRepository;
import com.codingmyeonga.localstep.auth.exception.ApiException;
import org.springframework.http.HttpStatus;

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
    private final QuestRepository questRepository;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    
    // 루트에 포함된 상점만 인정: 팀원 구현 전까지는 어댑터를 통해 조회하도록 추상화
    private final RouteStoreLookupPort routeStoreLookupPort = new FallbackRouteStoreLookup();
    
    // 방문 시 지급할 포인트
    private static final Integer VISIT_POINTS = 100;
    
    // 상점 근처 기준 반경 (미터)
    private static final double NEARBY_RADIUS_METERS = 50.0;
    


    @Transactional
    public StoreVisitTestResponseDto createVisit(StoreVisitTestRequestDto requestDto) {
        
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(requestDto.getUserId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 사용자입니다.");
        }
        
        // 루트 존재 여부 확인
        if (!routeRepository.existsById(requestDto.getRouteId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 루트입니다.");
        }
        
        // 1. 중복 방문 체크
        if (isDuplicateVisit(requestDto.getUserId(), requestDto.getRouteId(), requestDto.getStoreId())) {
            return StoreVisitTestResponseDto.builder()
                    .visitId(null)
                    .pointsAwarded(0)
                    .message("이미 방문한 상점입니다.")
                    .build();
        }
        
        // 2. 상점 위치 가져오기 (루트에 포함된 상점만 인정) - store_id 기준
        StoreLocation storeLocation = getStoreLocationFromRouteByStoreId(requestDto.getRouteId(), requestDto.getStoreId());
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
        
        // 5. 퀘스트 생성 및 포인트 지급 연동
        Quest quest = Quest.builder()
                .userId(requestDto.getUserId())
                .questType(Quest.QuestType.STORE_VISIT)
                .targetStoreId(requestDto.getStoreId())
                .rewardPoints(VISIT_POINTS)
                .build();
        Quest savedQuest = questRepository.save(quest);

        pointService.addPoints(
            requestDto.getUserId().longValue(), 
            VISIT_POINTS, 
            PointHistory.PointReason.STORE_VISIT, 
            savedVisit.getVisitId(), 
            savedQuest.getQuestId(),
            LocalDate.now()
        );
        
        return StoreVisitTestResponseDto.builder()
                .visitId(savedVisit.getVisitId())
                .pointsAwarded(VISIT_POINTS)
                .message("방문 기록이 저장되고 포인트가 지급되었습니다.")
                .build();
    }
    
    private boolean isDuplicateVisit(Long userId, Long routeId, Long storeId) {
        return storeVisitRepository.existsByUserIdAndRouteIdAndStoreId(userId, routeId, storeId);
    }
    
    private StoreLocation getStoreLocationFromRouteByStoreId(Long routeId, Long storeId) {
        return routeStoreLookupPort.findStoreInRouteByStoreId(routeId, storeId);
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
    public List<StoreVisitResponseDto> getUserVisits(Long userId, LocalDate startDate, LocalDate endDate) {
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 사용자입니다.");
        }
        
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
    

    private StoreVisitResponseDto convertToResponseDto(StoreVisit visit) {
        return StoreVisitResponseDto.builder()
                .visitId(visit.getVisitId())
                .storeId(visit.getStoreId())
                .storeName(getStoreName(visit.getStoreId())) // 상점 이름 가져오기
                .visitedAt(visit.getVisitedAt())
                .pointsAwarded(visit.getPointsAwarded())
                .build();
    }
    

    private String getStoreName(Long storeId) {
        // TODO: 실제 상점 API에서 상점 이름 조회
        // 현재는 기본값 사용
        switch (storeId.intValue()) {
            case 1:
                return "테스트 상점 1";
            case 2:
                return "테스트 상점 2";
            default:
                return "알 수 없는 상점";
        }
    }
    
    /**
     * 사용자의 위치를 받아서 근처 상점을 확인하고 자동 방문을 처리합니다.
     * @param requestDto 위치 정보
     * @return 자동 방문 처리 결과
     */
    @Transactional
    public LocationResponseDto processLocationAndAutoVisit(LocationRequestDto requestDto) {
        
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(requestDto.getUserId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 사용자입니다.");
        }
        
        // 루트 존재 여부 확인
        if (!routeRepository.existsById(requestDto.getRouteId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 루트입니다.");
        }
        
        // 1. 현재 루트의 상점 목록 조회 후, 사용자와 가장 가까운 상점 선별
        StoreLocation nearbyStore = findNearbyStoreInRoute(
                requestDto.getRouteId(), requestDto.getLatitude(), requestDto.getLongitude()
        );
        
        if (nearbyStore == null) {
            return LocationResponseDto.builder()
                    .nearbyStoreDetected(false)
                    .storeId(null)
                    .visitId(null)
                    .pointsAwarded(0)
                    .message("상점 방문이 감지되지 않아 포인트가 지급되지 않았습니다.")
                    .build();
        }
        
        // 2. 중복 방문 체크 (userId, routeId, storeId)
        if (isDuplicateVisit(requestDto.getUserId(), requestDto.getRouteId(), nearbyStore.storeId)) {
            return LocationResponseDto.builder()
                    .nearbyStoreDetected(true)
                    .storeId(nearbyStore.storeId)
                    .visitId(null)
                    .pointsAwarded(0)
                    .message("이미 방문한 상점입니다.")
                    .build();
        }
        
        // 3. 자동 방문 기록 생성 (storeId에는 store_id 저장)
        StoreVisit storeVisit = StoreVisit.builder()
                .userId(requestDto.getUserId())
                .routeId(requestDto.getRouteId())
                .storeId(nearbyStore.storeId) // store_id
                .userLatitude(requestDto.getLatitude())
                .userLongitude(requestDto.getLongitude())
                .visitedAt(requestDto.getTimestamp())
                .pointsAwarded(VISIT_POINTS)
                .build();
        
        StoreVisit savedVisit = storeVisitRepository.save(storeVisit);
        
        // 4. 퀘스트 생성 및 포인트 지급 연동
        Quest quest = Quest.builder()
                .userId(requestDto.getUserId())
                .questType(Quest.QuestType.STORE_VISIT)
                .targetStoreId(nearbyStore.storeId)
                .rewardPoints(VISIT_POINTS)
                .build();
        Quest savedQuest = questRepository.save(quest);

        pointService.addPoints(
            requestDto.getUserId().longValue(), 
            VISIT_POINTS, 
            PointHistory.PointReason.STORE_VISIT, 
            savedVisit.getVisitId(), 
            savedQuest.getQuestId(),
            LocalDate.now()
        );
        
        return LocationResponseDto.builder()
                .nearbyStoreDetected(true)
                .storeId(nearbyStore.storeId)
                .visitId(savedVisit.getVisitId())
                .pointsAwarded(VISIT_POINTS)
                .message("자동 방문이 기록되고 포인트가 지급되었습니다.")
                .build();
    }
    
    /**
     * 사용자 위치 근처의 상점을 찾습니다.
     * @param userLat 사용자 위도
     * @param userLng 사용자 경도
     * @return 근처 상점 정보 (없으면 null)
     */
    private StoreLocation findNearbyStoreInRoute(Long routeId, BigDecimal userLat, BigDecimal userLng) {
        List<StoreLocation> stores = routeStoreLookupPort.findStoresInRoute(routeId);
        StoreLocation best = null;
        double bestDist = Double.MAX_VALUE;
        for (StoreLocation s : stores) {
            if (s.latitude == null || s.longitude == null) continue;
            double d = calculateDistance(
                    userLat.doubleValue(), userLng.doubleValue(),
                    s.latitude.doubleValue(), s.longitude.doubleValue()
            );
            if (d <= NEARBY_RADIUS_METERS && d < bestDist) {
                best = s;
                bestDist = d;
            }
        }
        return best;
    }
    
    // 상점 위치 정보를 담는 내부 클래스
    private static class StoreLocation {
        private final Long storeId; // store_id 저장
        private final BigDecimal latitude; // store_lat
        private final BigDecimal longitude; // store_lng
        private StoreLocation(Long storeId, BigDecimal latitude, BigDecimal longitude) {
            this.storeId = storeId;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    /**
     * 팀원 구현 전까지의 임시 포트/어댑터. 팀원 코드가 추가되면 이 구현만 대체하면 됨.
     */
    private interface RouteStoreLookupPort {
        StoreLocation findStoreInRouteByStoreId(Long routeId, Long storeId);
        List<StoreLocation> findStoresInRoute(Long routeId);
    }

    private static class FallbackRouteStoreLookup implements RouteStoreLookupPort {
        @Override
        public StoreLocation findStoreInRouteByStoreId(Long routeId, Long storeId) {
            return null; // 아직 DB 연동 전: 존재하지 않는 것으로 처리
        }

        @Override
        public List<StoreLocation> findStoresInRoute(Long routeId) {
            return List.of(); // 아직 DB 연동 전: 빈 목록
        }
    }
}

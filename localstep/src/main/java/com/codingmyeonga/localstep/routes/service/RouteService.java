package com.codingmyeonga.localstep.routes.service;

import com.codingmyeonga.localstep.routes.dto.GenerateRouteRequest;
import com.codingmyeonga.localstep.routes.dto.RouteResponse;
//import com.codingmyeonga.localstep.routes.dto.StoreInRouteResponse;
import com.codingmyeonga.localstep.routes.dto.StoreInRouteResponse;
import com.codingmyeonga.localstep.routes.entity.Route;
import com.codingmyeonga.localstep.routes.entity.RouteStore;
import com.codingmyeonga.localstep.routes.external.ExternalMapService;
import com.codingmyeonga.localstep.routes.repository.RouteRepository;
import com.codingmyeonga.localstep.routes.dto.PlaceDto;
import com.codingmyeonga.localstep.routes.repository.RouteStoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
public class RouteService {
    private final RouteRepository routeRepository;
    private final ExternalMapService external; // ExternalMapService 주입 추가
    private final RouteStoreRepository routeStoreRepository;
    // 프랜차이즈 키워드
    private static final List<String> FRANCHISE = List.of(
            "스타벅스", "이디야", "투썸플레이스", "파리바게뜨", "던킨", "베스킨라빈스", "폴바셋", "탐앤탐스", "할리스",
            "공차", "맥도날드", "버거킹", "KFC", "롯데리아", "써브웨이", "쉑쉑", "빽다방", "메가커피",
            "이마트에브리데이", "GS25", "CU", "세븐일레븐", "더벤티", "컴포즈", "블루보틀", "카페베네", "다이소", "롯데", "이마트","GS"
    );

    // 루트 생성
    @Transactional
    public RouteResponse generate(GenerateRouteRequest req) {
        // 1) 루트 저장
        Route saved = routeRepository.save(
                new Route(req.getUser_id(), req.getUser_lat(), req.getUser_lng(), req.getGoal_steps())
        );

        // 2) 후보 수집
        List<PlaceDto> candidates = external.searchStores(req.getUser_lng(), req.getUser_lat(), 700);
        if (candidates == null) {
            candidates = new ArrayList<>();
        }
        System.out.println("[routes] candidates=" + candidates.size());
        // 3) 필터(프랜차이즈 해당할 경우 제거)
        List<PlaceDto> filtered = new ArrayList<>();
        for (int i = 0; i < candidates.size(); i++) {
            PlaceDto p = candidates.get(i);
            if (notFranchise(p)) {
                filtered.add(p);
            }
        }
        System.out.println("[routes] filtered=" + filtered.size());
        // 4) k 계산(K: 목표 걸음 수 기반으로 경유 상점 개수 설정)
        int k = StoreNumBySteps(req.getGoal_steps());
        System.out.println("[routes] k=" + k);

        // 5) 기본 풀(비면 candidates로 대체)
        List<PlaceDto> base = new ArrayList<>();
        if (filtered.size() == 0) {
            for (int i = 0; i < candidates.size(); i++) {
                base.add(candidates.get(i));
            }
        } else {
            for (int i = 0; i < filtered.size(); i++) {
                base.add(filtered.get(i));
            }
        }
        // 6) 부족하면 candidates에서 보충(이름으로 중복 제거)
        if (base.size() < k) {
            Set<String> seen = new HashSet<>();
            for (PlaceDto p : base) seen.add(p.getStore_name());
            for (PlaceDto c : candidates) {
                if (base.size() >= k * 2) break;
                if (!seen.contains(c.getStore_name())) {
                    base = new ArrayList<>(base);
                    base.add(c);
                    seen.add(c.getStore_name());
                }
            }
        }

        // 7) 섞고 앞에서 k개
        List<PlaceDto> pool = new ArrayList<>(base);
        Collections.shuffle(pool, new Random(ThreadLocalRandom.current().nextLong()));
        int take = Math.min(k, pool.size());
        List<PlaceDto> chosen = pool.subList(0, take);
//chosen 저장
        int order = 1;
        for (PlaceDto p : chosen) {
            routeStoreRepository.save(
                    RouteStore.builder()
                            .route(saved)
                            .storeId(p.getStore_id())
                            .orderInRoute(order++)
                            .storeName(p.getStore_name())
                            .storeAddress(p.getStore_address())
                            .storeLat(p.getStore_lat())
                            .storeLng(p.getStore_lng())
                            .storeUrl(p.getStore_url())
                            .build()
            );
        }return toResponse(saved);

    }

    //프렌차이즈 아닌 상점들만 필터링
    private boolean notFranchise(PlaceDto p) {
        String name = normalize(p.getStore_name());
        //프랜차이즈 중 하나라도 해당되는지 확인
        for (String f : FRANCHISE) {
            //문자열 null 이면 false 리턴
            if (name.contains(normalize(f))) {
                return false;
            }
        }
        return true;
    }

    private String normalize(String s) {
        //들어온 문자열이 NULL이면 "" 리턴
        if (s == null) {
            return "";
        }
        //아니면 모든 공백을 없애고 + 소문자로 바꿔서 리턴
        return s.replaceAll("\\s+", "").toLowerCase();
    }

    //경유 상점 개수 설정 메서드
    //목표 걸음 수 700 마다 경유 상점 +1
    private int StoreNumBySteps(int steps) {
        int k = steps / 700;
        return Math.max(2, k);   // 몫이 0일 경우를 방지 -> 최소 2개의 상점을 경유하도록 함.
    }

    //userId, date로 루트 조회
    public List<RouteResponse> getByUserAndDate(Long userId, LocalDate date) {
        //start, end  기준 잡기
        var start = date.atStartOfDay();
        var end = date.atTime(23, 59, 59);
        //userId + 생성날짜로 DB 조회
        List<Route> routes = routeRepository.findAllByUserIdAndCreatedAtBetween(userId, start, end);
        //엔티티 -> toResponse 메서드 통해 dto 변환
        return routes.stream().map(this::toResponse).toList();
    }

    //루트 완료 처리(routeId로)
    public RouteResponse complete(Long routeId) {
        //해당 루트 없을 시 예외처리
        Route r = routeRepository.findById(routeId).orElseThrow(() -> new IllegalArgumentException("해당 루트 없음"));
        r.complete(); //완료로 변경
        return toResponse(routeRepository.save(r));
    }
    private RouteResponse toResponse(Route r) {
        List<RouteStore> routeStores =
                routeStoreRepository.findAllByRouteIdOrderByOrderInRouteAsc(r.getId());

        List<String> names = new ArrayList<>();
        for (int i = 0; i < routeStores.size(); i++) {
            RouteStore rs = routeStores.get(i);
            names.add(rs.getStoreName());
        }
        List<StoreInRouteResponse> stores = new ArrayList<>();
        for (int i = 0; i < routeStores.size(); i++) {
            RouteStore rs = routeStores.get(i);

            StoreInRouteResponse dto = StoreInRouteResponse.builder()
                    .route_store_id(rs.getId())
                    .store_id(rs.getStoreId())
                    .order_in_route(rs.getOrderInRoute())
                    .store_name(rs.getStoreName())
                    .store_address(rs.getStoreAddress())
                    .store_lat(rs.getStoreLat())
                    .store_lng(rs.getStoreLng())
                    .store_url(rs.getStoreUrl())
                    .build();

            stores.add(dto);
        }

        RouteResponse result = new RouteResponse();
        result.setRoute_id(r.getId());
        result.setUser_id(r.getUserId());
        result.setUser_lat(r.getUserLat());
        result.setUser_lng(r.getUserLng());
        result.setGoal_steps(r.getGoal_steps());
        result.setIs_completed(Boolean.TRUE.equals(r.getCompleted()));
        result.setCreated_at(r.getCreatedAt());
        result.setCompleted_at(r.getCompletedAt());
        result.setStore_names(names);
        result.setStores(stores);

        return result;
    }
//    private RouteResponse toResponse(Route r) {
//        List<String> names = routeStoreRepository
//                .findAllByRouteIdOrderByOrderInRouteAsc(r.getId())
//                .stream()
//                .map(RouteStore::getStoreName)
//                .toList();
//
//        return RouteResponse.builder()
//                .route_id(r.getId())
//                .user_id(r.getUserId())
//                .user_lat(r.getUserLat())
//                .user_lng(r.getUserLng())
//                .goal_steps(r.getGoal_steps())
//                .is_completed(Boolean.TRUE.equals(r.getCompleted()))
//                .created_at(r.getCreatedAt())
//                .completed_at(r.getCompletedAt())
//                .store_names(names)
//                .build();
//    }

//    private RouteResponse toResponse(Route r, List<String> names) {
//        return RouteResponse.builder()
//                .route_id(r.getId())
//                .user_id(r.getUserId())
//                .user_lat(r.getUserLat())
//                .user_lng(r.getUserLng())
//                .goal_steps(r.getGoal_steps())
//                .is_completed(Boolean.TRUE.equals(r.getCompleted()))
//                .created_at(r.getCreatedAt())
//                .completed_at(r.getCompletedAt())
//                .store_names(names)
//                .build();
//    }

    //상점 상세 조회
    public StoreInRouteResponse getStoreDetail(Long routeId, Integer orderInRoute) {
        RouteStore rs = routeStoreRepository
                .findByRouteIdAndOrderInRoute(routeId, orderInRoute)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 순번의 상점이 없어요."));

        return StoreInRouteResponse.builder()
                .route_store_id(rs.getId())
                .store_id(Long.valueOf(rs.getOrderInRoute())) // ← 필요하면 순번을 store_id처럼 내려줘도 됨
                .order_in_route(rs.getOrderInRoute())
                .store_name(rs.getStoreName())
                .store_address(rs.getStoreAddress())
                .store_lat(rs.getStoreLat())
                .store_lng(rs.getStoreLng())
                .store_url(rs.getStoreUrl())
                .build();
    }

}




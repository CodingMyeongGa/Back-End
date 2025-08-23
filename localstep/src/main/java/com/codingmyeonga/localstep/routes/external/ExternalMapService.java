package com.codingmyeonga.localstep.routes.external;

import com.codingmyeonga.localstep.config.NaverDevHeaderProvider;
import com.codingmyeonga.localstep.routes.dto.PlaceDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ExternalMapService {

    private final RestTemplate rt;
    private final NaverDevHeaderProvider devHeaders;
    private final ObjectMapper om = new ObjectMapper();

    public List<PlaceDto> searchStores(double lng, double lat, int radius) {
        // 같은 지역에 대해 다양한 카테고리로 뽑아서 풀을 키움
        String[] queries = {
                "남가좌 맛집", "남가좌 한식", "남가좌 일식", "남가좌 중식", "남가좌 카페", "남가좌 디저트",
                "남가좌 슈퍼","남가좌 "
        };

        // 제목(상호명) 기준으로 중복 제거
        Map<String, PlaceDto> dedup = new LinkedHashMap<>();

        for (String q : queries) {
            int display = 50; // 넉넉히
            int start = 1 + ThreadLocalRandom.current().nextInt(20); // 페이지 랜덤

            String url = UriComponentsBuilder
                    .fromUriString("https://openapi.naver.com/v1/search/local.json")
                    .queryParam("query", q)
                    .queryParam("display", display)
                    .queryParam("start", start)
                    .queryParam("sort", "random") // ★ 랜덤 정렬
                    .build()
                    .toUriString();

            ResponseEntity<String> res = rt.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(devHeaders.headers()), String.class
            );
            String raw = res.getBody();
            if (raw == null || !res.getStatusCode().is2xxSuccessful()) continue;

            try {
                JsonNode items = om.readTree(raw).path("items");
                if (items.isArray()) {
                    for (JsonNode it : items) {
                        String title = clean(it.path("title").asText());
                        String addr = it.path("roadAddress").asText();
                        if (addr == null || addr.isBlank()) addr = it.path("address").asText();
                        //실제 주소에 남가좌동 들어가야 함
//                        if (addr == null || !addr.contains("남가좌")) {
//                            continue;
//                        }
                        double lon = 0.0, lat2 = 0.0;
                        try {
                            lon = Long.parseLong(it.path("mapx").asText()) / 1e7;
                            lat2 = Long.parseLong(it.path("mapy").asText()) / 1e7;
                        } catch (NumberFormatException ignore) {
                        }

                        // 제목 기준 dedup

                        long fakeId = Objects.hash(title, addr);  // 간단히 id 생성
                        String urlLink = "https://map.naver.com/v5/search/" + title;

                        dedup.putIfAbsent(title, new PlaceDto(
                                fakeId,        // store_id
                                title,         // store_name
                                lat2,          // store_lat
                                lon,           // store_lng
                                addr,          // store_address
                                urlLink        // store_url
                        ));

                    }
                }
            } catch (Exception ignore) {
            }
        }

        return new ArrayList<>(dedup.values());
    }

    private String clean(String s) {
        return s == null ? "" : s.replaceAll("<.*?>", "");
    }
}

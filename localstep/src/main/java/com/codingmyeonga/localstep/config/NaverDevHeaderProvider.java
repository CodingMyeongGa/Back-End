package com.codingmyeonga.localstep.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class NaverDevHeaderProvider {
    @Value("${naverdev.client-id}")
    private String clientId;

    @Value("${naverdev.client-secret}")
    private String clientSecret;

    public HttpHeaders headers() {
        System.out.println("[dev-headers] idLen=" + (clientId == null ? 0 : clientId.length())
                + ", secretLen=" + (clientSecret == null ? 0 : clientSecret.length()));
        HttpHeaders h = new HttpHeaders();
        h.set("X-Naver-Client-Id", clientId);
        h.set("X-Naver-Client-Secret", clientSecret);
        return h;
    }
}

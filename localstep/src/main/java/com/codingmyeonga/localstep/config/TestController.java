package com.codingmyeonga.localstep.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "포인트 지급 관련")
public class TestController {

    @GetMapping("/test")
    @Operation(summary = "테스트 API", description = "Swagger가 정상 작동하는지 테스트하는 API")
    public String test() {
        return "Swagger is working!";
    }
}

package com.terabyte.bapjigi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 홈 화면을 처리하는 컨트롤러
 */
@RestController
public class HomeController {

    /**
     * 루트 경로 접근 시 기본 응답 반환
     * @return 환영 메시지
     */
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("밥지기 API 서버에 오신 것을 환영합니다!");
    }
} 
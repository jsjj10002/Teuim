package com.terabyte.bapjigi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 홈 화면을 처리하는 컨트롤러
 */
@Controller
public class HomeController {

    /**
     * 루트 경로 접근 시 홈 페이지 반환
     * @return 홈 페이지 뷰 이름
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * API 테스트 페이지 반환
     * @return API 테스트 페이지 뷰 이름
     */
    @GetMapping("/api-test")
    public String apiTest() {
        return "api-test";
    }
} 
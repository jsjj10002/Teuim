package com.terabyte.bapjigi.controller;

import com.terabyte.bapjigi.service.RankingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/food-expense")
    public ResponseEntity<List<Map<String, Object>>> getFoodExpenseRanking() {
        List<Map<String, Object>> rankings = rankingService.getFoodExpenseRanking();
        return ResponseEntity.ok(rankings);
    }

    @GetMapping("/food-expense/period")
    public ResponseEntity<List<Map<String, Object>>> getFoodExpenseRankingByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Map<String, Object>> rankings = rankingService.getFoodExpenseRankingByPeriod(startDate, endDate);
        return ResponseEntity.ok(rankings);
    }

    @GetMapping("/my-ranking")
    public ResponseEntity<Map<String, Object>> getMyRankingInfo() {
        Map<String, Object> myRanking = rankingService.getMyRankingInfo();
        return ResponseEntity.ok(myRanking);
    }
} 
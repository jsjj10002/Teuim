package com.terabyte.bapjigi.controller;

import com.terabyte.bapjigi.dto.MealPlanDto;
import com.terabyte.bapjigi.model.MealPlan;
import com.terabyte.bapjigi.service.MealPlanService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 식단표 관련 API를 처리하는 컨트롤러
 * 식단표 생성, 조회, 수정, 삭제 및 AI 식단표 생성 기능 제공
 */
@RestController
@RequestMapping("/api/meal-plans")
public class MealPlanController {

    private final MealPlanService mealPlanService;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    /**
     * 식단표 생성
     * @param mealPlanDto 식단표 정보
     * @return 생성 성공 메시지
     */
    @PostMapping
    public ResponseEntity<?> createMealPlan(@Valid @RequestBody MealPlanDto mealPlanDto) {
        try {
            MealPlan mealPlan = mealPlanService.createMealPlan(mealPlanDto);
            return ResponseEntity.ok("식단표가 생성되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * AI를 이용한 식단표 자동 생성
     * @param date 식단표 날짜
     * @return 생성 성공 메시지
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateAIMealPlan(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            MealPlan mealPlan = mealPlanService.generateAIMealPlan(date);
            return ResponseEntity.ok("AI가 식단표를 생성했습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 모든 식단표 조회
     * @return 사용자의 모든 식단표 목록
     */
    @GetMapping
    public ResponseEntity<List<MealPlanDto>> getAllMealPlans() {
        List<MealPlanDto> mealPlans = mealPlanService.getAllMealPlans();
        return ResponseEntity.ok(mealPlans);
    }

    /**
     * 날짜 범위로 식단표 조회
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 지정된 기간 내의 식단표 목록
     */
    @GetMapping("/byDateRange")
    public ResponseEntity<List<MealPlanDto>> getMealPlansByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<MealPlanDto> mealPlans = mealPlanService.getMealPlansByDateRange(startDate, endDate);
        return ResponseEntity.ok(mealPlans);
    }

    /**
     * 특정 날짜의 식단표 조회
     * @param date 조회할 날짜
     * @return 해당 날짜의 식단표
     */
    @GetMapping("/byDate")
    public ResponseEntity<?> getMealPlanByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            MealPlanDto mealPlanDto = mealPlanService.getMealPlanByDate(date);
            return ResponseEntity.ok(mealPlanDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 식단표 수정
     * @param date 수정할 식단표의 날짜
     * @param mealPlanDto 수정할 정보
     * @return 수정 성공 메시지
     */
    @PutMapping("/byDate")
    public ResponseEntity<?> updateMealPlan(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody MealPlanDto mealPlanDto) {
        try {
            MealPlan updatedMealPlan = mealPlanService.updateMealPlan(date, mealPlanDto);
            return ResponseEntity.ok("식단표가 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 식단표 삭제
     * @param date 삭제할 식단표의 날짜
     * @return 삭제 성공 메시지
     */
    @DeleteMapping("/byDate")
    public ResponseEntity<?> deleteMealPlan(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            mealPlanService.deleteMealPlan(date);
            return ResponseEntity.ok("식단표가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 
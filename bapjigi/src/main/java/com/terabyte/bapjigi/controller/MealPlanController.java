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

@RestController
@RequestMapping("/api/meal-plans")
public class MealPlanController {

    private final MealPlanService mealPlanService;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @PostMapping
    public ResponseEntity<?> createMealPlan(@Valid @RequestBody MealPlanDto mealPlanDto) {
        try {
            MealPlan mealPlan = mealPlanService.createMealPlan(mealPlanDto);
            return ResponseEntity.ok("식단표가 생성되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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

    @GetMapping
    public ResponseEntity<List<MealPlanDto>> getAllMealPlans() {
        List<MealPlanDto> mealPlans = mealPlanService.getAllMealPlans();
        return ResponseEntity.ok(mealPlans);
    }

    @GetMapping("/byDateRange")
    public ResponseEntity<List<MealPlanDto>> getMealPlansByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<MealPlanDto> mealPlans = mealPlanService.getMealPlansByDateRange(startDate, endDate);
        return ResponseEntity.ok(mealPlans);
    }

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
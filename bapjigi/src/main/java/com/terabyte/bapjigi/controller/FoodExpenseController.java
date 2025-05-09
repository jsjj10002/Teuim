package com.terabyte.bapjigi.controller;

import com.terabyte.bapjigi.dto.FoodExpenseDto;
import com.terabyte.bapjigi.model.FoodExpense;
import com.terabyte.bapjigi.service.FoodExpenseService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/food-expenses")
public class FoodExpenseController {

    private final FoodExpenseService foodExpenseService;

    public FoodExpenseController(FoodExpenseService foodExpenseService) {
        this.foodExpenseService = foodExpenseService;
    }

    @PostMapping
    public ResponseEntity<?> addFoodExpense(@Valid @RequestBody FoodExpenseDto foodExpenseDto) {
        try {
            FoodExpense foodExpense = foodExpenseService.addFoodExpense(foodExpenseDto);
            return ResponseEntity.ok("식비 지출이 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<FoodExpenseDto>> getAllFoodExpenses() {
        List<FoodExpenseDto> foodExpenses = foodExpenseService.getAllFoodExpenses();
        return ResponseEntity.ok(foodExpenses);
    }

    @GetMapping("/byDateRange")
    public ResponseEntity<List<FoodExpenseDto>> getFoodExpensesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<FoodExpenseDto> foodExpenses = foodExpenseService.getFoodExpensesByDateRange(startDate, endDate);
        return ResponseEntity.ok(foodExpenses);
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, Integer>> getTotalFoodExpenseByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Integer total = foodExpenseService.getTotalFoodExpenseByDateRange(startDate, endDate);
        return ResponseEntity.ok(Map.of("total", total));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFoodExpenseById(@PathVariable Long id) {
        try {
            FoodExpenseDto foodExpenseDto = foodExpenseService.getFoodExpenseById(id);
            return ResponseEntity.ok(foodExpenseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFoodExpense(@PathVariable Long id, 
                                              @Valid @RequestBody FoodExpenseDto foodExpenseDto) {
        try {
            FoodExpense updatedFoodExpense = foodExpenseService.updateFoodExpense(id, foodExpenseDto);
            return ResponseEntity.ok("식비 지출이 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFoodExpense(@PathVariable Long id) {
        try {
            foodExpenseService.deleteFoodExpense(id);
            return ResponseEntity.ok("식비 지출이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 
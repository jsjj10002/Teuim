package com.terabyte.bapjigi.controller;

import com.terabyte.bapjigi.dto.BudgetGoalDto;
import com.terabyte.bapjigi.model.BudgetGoal;
import com.terabyte.bapjigi.service.BudgetGoalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budget-goals")
public class BudgetGoalController {

    private final BudgetGoalService budgetGoalService;

    public BudgetGoalController(BudgetGoalService budgetGoalService) {
        this.budgetGoalService = budgetGoalService;
    }

    @PostMapping
    public ResponseEntity<?> createBudgetGoal(@Valid @RequestBody BudgetGoalDto budgetGoalDto) {
        try {
            BudgetGoal budgetGoal = budgetGoalService.createBudgetGoal(budgetGoalDto);
            return ResponseEntity.ok("예산 목표가 설정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<BudgetGoalDto>> getAllBudgetGoals() {
        List<BudgetGoalDto> budgetGoals = budgetGoalService.getAllBudgetGoals();
        return ResponseEntity.ok(budgetGoals);
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentBudgetGoal() {
        BudgetGoalDto budgetGoalDto = budgetGoalService.getCurrentBudgetGoal();
        if (budgetGoalDto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(budgetGoalDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBudgetGoalById(@PathVariable Long id) {
        try {
            BudgetGoalDto budgetGoalDto = budgetGoalService.getBudgetGoalById(id);
            return ResponseEntity.ok(budgetGoalDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBudgetGoal(@PathVariable Long id, 
                                             @Valid @RequestBody BudgetGoalDto budgetGoalDto) {
        try {
            BudgetGoal updatedBudgetGoal = budgetGoalService.updateBudgetGoal(id, budgetGoalDto);
            return ResponseEntity.ok("예산 목표가 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudgetGoal(@PathVariable Long id) {
        try {
            budgetGoalService.deleteBudgetGoal(id);
            return ResponseEntity.ok("예산 목표가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 
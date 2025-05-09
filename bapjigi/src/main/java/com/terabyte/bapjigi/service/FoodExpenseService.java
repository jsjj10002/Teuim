package com.terabyte.bapjigi.service;

import com.terabyte.bapjigi.dto.FoodExpenseDto;
import com.terabyte.bapjigi.model.BudgetGoal;
import com.terabyte.bapjigi.model.FoodExpense;
import com.terabyte.bapjigi.model.User;
import com.terabyte.bapjigi.repository.BudgetGoalRepository;
import com.terabyte.bapjigi.repository.FoodExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FoodExpenseService {

    private final FoodExpenseRepository foodExpenseRepository;
    private final BudgetGoalRepository budgetGoalRepository;
    private final UserService userService;

    public FoodExpenseService(FoodExpenseRepository foodExpenseRepository,
                              BudgetGoalRepository budgetGoalRepository,
                              UserService userService) {
        this.foodExpenseRepository = foodExpenseRepository;
        this.budgetGoalRepository = budgetGoalRepository;
        this.userService = userService;
    }

    @Transactional
    public FoodExpense addFoodExpense(FoodExpenseDto foodExpenseDto) {
        User currentUser = userService.getCurrentUser();
        
        // 해당 날짜에 맞는 예산 목표 찾기
        Optional<BudgetGoal> budgetGoal;
        if (foodExpenseDto.getBudgetGoalId() != null) {
            budgetGoal = budgetGoalRepository.findById(foodExpenseDto.getBudgetGoalId());
        } else {
            LocalDate expenseDate = foodExpenseDto.getDate();
            budgetGoal = budgetGoalRepository.findByUserAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                    currentUser, expenseDate, expenseDate).stream().findFirst();
        }
        
        FoodExpense foodExpense = FoodExpense.builder()
                .user(currentUser)
                .budgetGoal(budgetGoal.orElse(null))
                .amount(foodExpenseDto.getAmount())
                .date(foodExpenseDto.getDate())
                .description(foodExpenseDto.getDescription())
                .mealType(foodExpenseDto.getMealType())
                .build();
        
        return foodExpenseRepository.save(foodExpense);
    }

    @Transactional(readOnly = true)
    public List<FoodExpenseDto> getAllFoodExpenses() {
        User currentUser = userService.getCurrentUser();
        List<FoodExpense> foodExpenses = foodExpenseRepository.findByUserOrderByDateDesc(currentUser);
        
        return foodExpenses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FoodExpenseDto> getFoodExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        User currentUser = userService.getCurrentUser();
        List<FoodExpense> foodExpenses = foodExpenseRepository.findByUserAndDateBetweenOrderByDateDesc(
                currentUser, startDate, endDate);
        
        return foodExpenses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FoodExpenseDto getFoodExpenseById(Long id) {
        User currentUser = userService.getCurrentUser();
        
        return foodExpenseRepository.findById(id)
                .filter(foodExpense -> foodExpense.getUser().getId().equals(currentUser.getId()))
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("해당 식비 지출을 찾을 수 없습니다."));
    }

    @Transactional
    public FoodExpense updateFoodExpense(Long id, FoodExpenseDto foodExpenseDto) {
        User currentUser = userService.getCurrentUser();
        
        FoodExpense foodExpense = foodExpenseRepository.findById(id)
                .filter(fe -> fe.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new RuntimeException("해당 식비 지출을 찾을 수 없습니다."));
        
        // 예산 목표 업데이트 여부 확인
        if (foodExpenseDto.getBudgetGoalId() != null) {
            BudgetGoal budgetGoal = budgetGoalRepository.findById(foodExpenseDto.getBudgetGoalId())
                    .orElseThrow(() -> new RuntimeException("해당 예산 목표를 찾을 수 없습니다."));
            foodExpense.setBudgetGoal(budgetGoal);
        }
        
        foodExpense.setAmount(foodExpenseDto.getAmount());
        foodExpense.setDate(foodExpenseDto.getDate());
        foodExpense.setDescription(foodExpenseDto.getDescription());
        foodExpense.setMealType(foodExpenseDto.getMealType());
        
        return foodExpenseRepository.save(foodExpense);
    }

    @Transactional
    public void deleteFoodExpense(Long id) {
        User currentUser = userService.getCurrentUser();
        
        FoodExpense foodExpense = foodExpenseRepository.findById(id)
                .filter(fe -> fe.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new RuntimeException("해당 식비 지출을 찾을 수 없습니다."));
        
        foodExpenseRepository.delete(foodExpense);
    }

    @Transactional(readOnly = true)
    public Integer getTotalFoodExpenseByDateRange(LocalDate startDate, LocalDate endDate) {
        User currentUser = userService.getCurrentUser();
        Integer total = foodExpenseRepository.sumAmountByUserAndDateBetween(currentUser, startDate, endDate);
        return total != null ? total : 0;
    }

    private FoodExpenseDto convertToDto(FoodExpense foodExpense) {
        return FoodExpenseDto.builder()
                .id(foodExpense.getId())
                .budgetGoalId(foodExpense.getBudgetGoal() != null ? foodExpense.getBudgetGoal().getId() : null)
                .amount(foodExpense.getAmount())
                .date(foodExpense.getDate())
                .description(foodExpense.getDescription())
                .mealType(foodExpense.getMealType())
                .build();
    }
} 
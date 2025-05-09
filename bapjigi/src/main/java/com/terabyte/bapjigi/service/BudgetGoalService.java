package com.terabyte.bapjigi.service;

import com.terabyte.bapjigi.dto.BudgetGoalDto;
import com.terabyte.bapjigi.model.BudgetGoal;
import com.terabyte.bapjigi.model.User;
import com.terabyte.bapjigi.repository.BudgetGoalRepository;
import com.terabyte.bapjigi.repository.FoodExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetGoalService {

    private final BudgetGoalRepository budgetGoalRepository;
    private final FoodExpenseRepository foodExpenseRepository;
    private final UserService userService;

    public BudgetGoalService(BudgetGoalRepository budgetGoalRepository, 
                             FoodExpenseRepository foodExpenseRepository,
                             UserService userService) {
        this.budgetGoalRepository = budgetGoalRepository;
        this.foodExpenseRepository = foodExpenseRepository;
        this.userService = userService;
    }

    @Transactional
    public BudgetGoal createBudgetGoal(BudgetGoalDto budgetGoalDto) {
        User currentUser = userService.getCurrentUser();
        
        BudgetGoal budgetGoal = BudgetGoal.builder()
                .user(currentUser)
                .targetAmount(budgetGoalDto.getTargetAmount())
                .startDate(budgetGoalDto.getStartDate())
                .endDate(budgetGoalDto.getEndDate())
                .build();
        
        return budgetGoalRepository.save(budgetGoal);
    }

    @Transactional(readOnly = true)
    public List<BudgetGoalDto> getAllBudgetGoals() {
        User currentUser = userService.getCurrentUser();
        List<BudgetGoal> budgetGoals = budgetGoalRepository.findByUserOrderByStartDateDesc(currentUser);
        
        return budgetGoals.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BudgetGoalDto getCurrentBudgetGoal() {
        User currentUser = userService.getCurrentUser();
        LocalDate today = LocalDate.now();
        
        return budgetGoalRepository.findByUserAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        currentUser, today, today)
                .stream()
                .findFirst()
                .map(this::convertToDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public BudgetGoalDto getBudgetGoalById(Long id) {
        User currentUser = userService.getCurrentUser();
        
        return budgetGoalRepository.findById(id)
                .filter(budgetGoal -> budgetGoal.getUser().getId().equals(currentUser.getId()))
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("해당 예산 목표를 찾을 수 없습니다."));
    }

    @Transactional
    public BudgetGoal updateBudgetGoal(Long id, BudgetGoalDto budgetGoalDto) {
        User currentUser = userService.getCurrentUser();
        
        BudgetGoal budgetGoal = budgetGoalRepository.findById(id)
                .filter(bg -> bg.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new RuntimeException("해당 예산 목표를 찾을 수 없습니다."));
        
        budgetGoal.setTargetAmount(budgetGoalDto.getTargetAmount());
        budgetGoal.setStartDate(budgetGoalDto.getStartDate());
        budgetGoal.setEndDate(budgetGoalDto.getEndDate());
        
        return budgetGoalRepository.save(budgetGoal);
    }

    @Transactional
    public void deleteBudgetGoal(Long id) {
        User currentUser = userService.getCurrentUser();
        
        BudgetGoal budgetGoal = budgetGoalRepository.findById(id)
                .filter(bg -> bg.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new RuntimeException("해당 예산 목표를 찾을 수 없습니다."));
        
        budgetGoalRepository.delete(budgetGoal);
    }

    private BudgetGoalDto convertToDto(BudgetGoal budgetGoal) {
        Integer spentAmount = foodExpenseRepository.sumAmountByBudgetGoal(budgetGoal);
        if (spentAmount == null) {
            spentAmount = 0;
        }
        
        Integer remainingAmount = budgetGoal.getTargetAmount() - spentAmount;
        Double progressPercentage = (double) spentAmount / budgetGoal.getTargetAmount() * 100;
        
        return BudgetGoalDto.builder()
                .id(budgetGoal.getId())
                .targetAmount(budgetGoal.getTargetAmount())
                .startDate(budgetGoal.getStartDate())
                .endDate(budgetGoal.getEndDate())
                .spentAmount(spentAmount)
                .remainingAmount(remainingAmount)
                .progressPercentage(progressPercentage)
                .build();
    }
} 
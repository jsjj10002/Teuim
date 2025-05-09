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

/**
 * 예산 목표 관련 비즈니스 로직을 처리하는 서비스
 * 예산 목표 생성, 조회, 수정, 삭제 기능 제공
 */
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

    /**
     * 예산 목표 생성
     * @param budgetGoalDto 예산 목표 정보
     * @return 생성된 예산 목표
     */
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

    /**
     * 사용자의 모든 예산 목표 조회
     * @return 예산 목표 목록
     */
    @Transactional(readOnly = true)
    public List<BudgetGoalDto> getAllBudgetGoals() {
        User currentUser = userService.getCurrentUser();
        List<BudgetGoal> budgetGoals = budgetGoalRepository.findByUserOrderByStartDateDesc(currentUser);
        
        return budgetGoals.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 현재 활성화된 예산 목표 조회
     * 현재 날짜가 시작일과 종료일 사이에 있는 목표
     * @return 현재 활성화된 예산 목표 또는 null
     */
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

    /**
     * 특정 ID로 예산 목표 조회
     * @param id 예산 목표 ID
     * @return 예산 목표
     */
    @Transactional(readOnly = true)
    public BudgetGoalDto getBudgetGoalById(Long id) {
        User currentUser = userService.getCurrentUser();
        
        return budgetGoalRepository.findById(id)
                .filter(budgetGoal -> budgetGoal.getUser().getId().equals(currentUser.getId()))
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("해당 예산 목표를 찾을 수 없습니다."));
    }

    /**
     * 예산 목표 업데이트
     * @param id 예산 목표 ID
     * @param budgetGoalDto 업데이트할 정보
     * @return 업데이트된 예산 목표
     */
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

    /**
     * 예산 목표 삭제
     * @param id 예산 목표 ID
     */
    @Transactional
    public void deleteBudgetGoal(Long id) {
        User currentUser = userService.getCurrentUser();
        
        BudgetGoal budgetGoal = budgetGoalRepository.findById(id)
                .filter(bg -> bg.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new RuntimeException("해당 예산 목표를 찾을 수 없습니다."));
        
        budgetGoalRepository.delete(budgetGoal);
    }

    /**
     * BudgetGoal 엔티티를 DTO로 변환
     * 추가로 소비 금액, 남은 금액, 진행률 정보 추가
     */
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
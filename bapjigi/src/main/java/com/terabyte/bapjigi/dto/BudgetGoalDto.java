package com.terabyte.bapjigi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetGoalDto {
    
    private Long id;
    
    @NotNull(message = "목표 금액은 필수 입력값입니다.")
    private Integer targetAmount;
    
    @NotNull(message = "시작 날짜는 필수 입력값입니다.")
    private LocalDate startDate;
    
    @NotNull(message = "종료 날짜는 필수 입력값입니다.")
    private LocalDate endDate;
    
    private Integer spentAmount;
    private Integer remainingAmount;
    private Double progressPercentage;
} 
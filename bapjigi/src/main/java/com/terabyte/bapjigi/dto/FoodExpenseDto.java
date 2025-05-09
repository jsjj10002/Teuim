package com.terabyte.bapjigi.dto;

import com.terabyte.bapjigi.model.FoodExpense;
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
public class FoodExpenseDto {
    
    private Long id;
    
    private Long budgetGoalId;
    
    @NotNull(message = "금액은 필수 입력값입니다.")
    private Integer amount;
    
    @NotNull(message = "날짜는 필수 입력값입니다.")
    private LocalDate date;
    
    private String description;
    
    private FoodExpense.MealType mealType;
} 
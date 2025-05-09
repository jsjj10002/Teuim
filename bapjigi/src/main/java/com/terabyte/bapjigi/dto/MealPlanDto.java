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
public class MealPlanDto {
    
    private Long id;
    
    @NotNull(message = "날짜는 필수 입력값입니다.")
    private LocalDate date;
    
    private String breakfast;
    
    private String lunch;
    
    private String dinner;
    
    private Integer estimatedCost;
    
    private boolean aiGenerated;
} 
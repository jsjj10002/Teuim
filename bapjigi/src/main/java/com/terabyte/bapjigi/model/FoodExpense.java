package com.terabyte.bapjigi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 사용자의 식비 지출 기록을 저장하는 엔티티 클래스
 * 식비 금액, 날짜, 설명 및 식사 유형 등을 관리
 */
@Entity
@Table(name = "food_expenses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 지출을 등록한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 연관된 예산 목표
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_goal_id")
    private BudgetGoal budgetGoal;

    // 지출 금액
    @Column(nullable = false)
    private Integer amount;

    // 지출 날짜
    @Column(nullable = false)
    private LocalDate date;

    // 지출 설명
    private String description;

    // 식사 유형 (아침, 점심, 저녁, 간식 등)
    @Enumerated(EnumType.STRING)
    private MealType mealType;

    // 생성 시간
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 수정 시간
    private LocalDateTime updatedAt;

    /**
     * 엔티티 생성 시 자동으로 현재 시간을 설정
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * 엔티티 수정 시 자동으로 현재 시간을 설정
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 식사 유형 열거형
     */
    public enum MealType {
        BREAKFAST, LUNCH, DINNER, SNACK, OTHER
    }
} 
package com.terabyte.bapjigi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_goal_id")
    private BudgetGoal budgetGoal;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private LocalDate date;

    private String description;

    @Enumerated(EnumType.STRING)
    private MealType mealType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum MealType {
        BREAKFAST, LUNCH, DINNER, SNACK, OTHER
    }
} 
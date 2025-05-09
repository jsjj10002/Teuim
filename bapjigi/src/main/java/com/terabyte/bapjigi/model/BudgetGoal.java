package com.terabyte.bapjigi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 사용자의 예산 목표 정보를 저장하는 엔티티 클래스
 * 식비 예산과 관련된 목표 금액 및 기간 정보를 관리
 */
@Entity
@Table(name = "budget_goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 목표를 설정한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 목표 금액
    @Column(nullable = false)
    private Integer targetAmount;

    // 목표 시작일
    @Column(nullable = false)
    private LocalDate startDate;

    // 목표 종료일
    @Column(nullable = false)
    private LocalDate endDate;

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
} 
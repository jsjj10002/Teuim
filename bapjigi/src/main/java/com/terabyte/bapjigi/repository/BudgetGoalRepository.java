package com.terabyte.bapjigi.repository;

import com.terabyte.bapjigi.model.BudgetGoal;
import com.terabyte.bapjigi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetGoalRepository extends JpaRepository<BudgetGoal, Long> {
    List<BudgetGoal> findByUser(User user);
    List<BudgetGoal> findByUserOrderByStartDateDesc(User user);
    Optional<BudgetGoal> findFirstByUserOrderByStartDateDesc(User user);
    List<BudgetGoal> findByUserAndStartDateLessThanEqualAndEndDateGreaterThanEqual(User user, LocalDate date, LocalDate sameDate);
} 
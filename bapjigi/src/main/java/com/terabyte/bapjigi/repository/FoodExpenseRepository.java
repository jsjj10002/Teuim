package com.terabyte.bapjigi.repository;

import com.terabyte.bapjigi.model.BudgetGoal;
import com.terabyte.bapjigi.model.FoodExpense;
import com.terabyte.bapjigi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FoodExpenseRepository extends JpaRepository<FoodExpense, Long> {
    List<FoodExpense> findByUser(User user);
    List<FoodExpense> findByUserOrderByDateDesc(User user);
    List<FoodExpense> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate);
    List<FoodExpense> findByBudgetGoal(BudgetGoal budgetGoal);
    
    @Query("SELECT SUM(f.amount) FROM FoodExpense f WHERE f.user = :user AND f.date BETWEEN :startDate AND :endDate")
    Integer sumAmountByUserAndDateBetween(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(f.amount) FROM FoodExpense f WHERE f.budgetGoal = :budgetGoal")
    Integer sumAmountByBudgetGoal(@Param("budgetGoal") BudgetGoal budgetGoal);
} 
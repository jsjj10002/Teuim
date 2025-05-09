package com.terabyte.bapjigi.repository;

import com.terabyte.bapjigi.model.MealPlan;
import com.terabyte.bapjigi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    List<MealPlan> findByUser(User user);
    List<MealPlan> findByUserOrderByDateDesc(User user);
    List<MealPlan> findByUserAndDateBetweenOrderByDate(User user, LocalDate startDate, LocalDate endDate);
    Optional<MealPlan> findByUserAndDate(User user, LocalDate date);
} 
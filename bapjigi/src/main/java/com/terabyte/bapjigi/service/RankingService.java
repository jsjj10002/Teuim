package com.terabyte.bapjigi.service;

import com.terabyte.bapjigi.model.User;
import com.terabyte.bapjigi.repository.FoodExpenseRepository;
import com.terabyte.bapjigi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RankingService {

    private final UserRepository userRepository;
    private final FoodExpenseRepository foodExpenseRepository;

    public RankingService(UserRepository userRepository, FoodExpenseRepository foodExpenseRepository) {
        this.userRepository = userRepository;
        this.foodExpenseRepository = foodExpenseRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getFoodExpenseRanking() {
        // 현재 월의 시작일과 종료일 계산
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();
        
        return getFoodExpenseRankingByPeriod(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getFoodExpenseRankingByPeriod(LocalDate startDate, LocalDate endDate) {
        List<User> users = userRepository.findAll();
        
        return users.stream()
                .map(user -> {
                    Integer totalAmount = foodExpenseRepository.sumAmountByUserAndDateBetween(user, startDate, endDate);
                    if (totalAmount == null) {
                        totalAmount = 0;
                    }
                    
                    Map<String, Object> userRanking = new HashMap<>();
                    userRanking.put("userId", user.getId());
                    userRanking.put("username", user.getUsername());
                    userRanking.put("profileName", user.getProfileName());
                    userRanking.put("profileImage", user.getProfileImage());
                    userRanking.put("totalAmount", totalAmount);
                    
                    return userRanking;
                })
                .filter(userRanking -> (Integer) userRanking.get("totalAmount") > 0)
                .sorted((a, b) -> ((Integer) a.get("totalAmount")).compareTo((Integer) b.get("totalAmount")))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getMyRankingInfo() {
        // 현재 월의 시작일과 종료일 계산
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();
        
        List<Map<String, Object>> rankings = getFoodExpenseRankingByPeriod(startDate, endDate);
        
        User currentUser = userRepository.findAll().get(0); // 임시로 첫 번째 사용자 반환 (실제로는 SecurityContext에서 가져와야 함)
        
        Map<String, Object> myRanking = rankings.stream()
                .filter(rank -> rank.get("userId").equals(currentUser.getId()))
                .findFirst()
                .orElse(null);
        
        if (myRanking == null) {
            myRanking = new HashMap<>();
            myRanking.put("userId", currentUser.getId());
            myRanking.put("username", currentUser.getUsername());
            myRanking.put("profileName", currentUser.getProfileName());
            myRanking.put("profileImage", currentUser.getProfileImage());
            myRanking.put("totalAmount", 0);
            myRanking.put("rank", rankings.size() + 1);
        } else {
            int rank = rankings.indexOf(myRanking) + 1;
            myRanking.put("rank", rank);
        }
        
        return myRanking;
    }
} 
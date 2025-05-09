package com.terabyte.bapjigi.service;

import com.terabyte.bapjigi.dto.MealPlanDto;
import com.terabyte.bapjigi.model.MealPlan;
import com.terabyte.bapjigi.model.User;
import com.terabyte.bapjigi.repository.MealPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final UserService userService;

    public MealPlanService(MealPlanRepository mealPlanRepository, UserService userService) {
        this.mealPlanRepository = mealPlanRepository;
        this.userService = userService;
    }

    @Transactional
    public MealPlan createMealPlan(MealPlanDto mealPlanDto) {
        User currentUser = userService.getCurrentUser();
        
        // 같은 날짜에 이미 식단표가 있는지 확인
        mealPlanRepository.findByUserAndDate(currentUser, mealPlanDto.getDate())
                .ifPresent(mealPlan -> {
                    throw new RuntimeException("해당 날짜에 이미 식단표가 존재합니다.");
                });
        
        MealPlan mealPlan = MealPlan.builder()
                .user(currentUser)
                .date(mealPlanDto.getDate())
                .breakfast(mealPlanDto.getBreakfast())
                .lunch(mealPlanDto.getLunch())
                .dinner(mealPlanDto.getDinner())
                .estimatedCost(mealPlanDto.getEstimatedCost())
                .aiGenerated(mealPlanDto.isAiGenerated())
                .build();
        
        return mealPlanRepository.save(mealPlan);
    }

    @Transactional
    public MealPlan generateAIMealPlan(LocalDate date) {
        User currentUser = userService.getCurrentUser();
        
        // AI로 식단표 생성 로직 (실제로는 외부 API를 호출하거나 복잡한 로직이 필요)
        // 여기서는 간단하게 샘플 데이터를 생성
        MealPlan mealPlan = MealPlan.builder()
                .user(currentUser)
                .date(date)
                .breakfast("AI 추천 아침 식단: 오트밀과 과일")
                .lunch("AI 추천 점심 식단: 현미밥과 닭가슴살 샐러드")
                .dinner("AI 추천 저녁 식단: 연어 스테이크와 구운 야채")
                .estimatedCost(15000)
                .aiGenerated(true)
                .build();
        
        return mealPlanRepository.save(mealPlan);
    }

    @Transactional(readOnly = true)
    public List<MealPlanDto> getAllMealPlans() {
        User currentUser = userService.getCurrentUser();
        List<MealPlan> mealPlans = mealPlanRepository.findByUserOrderByDateDesc(currentUser);
        
        return mealPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MealPlanDto> getMealPlansByDateRange(LocalDate startDate, LocalDate endDate) {
        User currentUser = userService.getCurrentUser();
        List<MealPlan> mealPlans = mealPlanRepository.findByUserAndDateBetweenOrderByDate(
                currentUser, startDate, endDate);
        
        return mealPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MealPlanDto getMealPlanByDate(LocalDate date) {
        User currentUser = userService.getCurrentUser();
        
        return mealPlanRepository.findByUserAndDate(currentUser, date)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("해당 날짜의 식단표를 찾을 수 없습니다."));
    }

    @Transactional
    public MealPlan updateMealPlan(LocalDate date, MealPlanDto mealPlanDto) {
        User currentUser = userService.getCurrentUser();
        
        MealPlan mealPlan = mealPlanRepository.findByUserAndDate(currentUser, date)
                .orElseThrow(() -> new RuntimeException("해당 날짜의 식단표를 찾을 수 없습니다."));
        
        mealPlan.setBreakfast(mealPlanDto.getBreakfast());
        mealPlan.setLunch(mealPlanDto.getLunch());
        mealPlan.setDinner(mealPlanDto.getDinner());
        mealPlan.setEstimatedCost(mealPlanDto.getEstimatedCost());
        mealPlan.setAiGenerated(mealPlanDto.isAiGenerated());
        
        return mealPlanRepository.save(mealPlan);
    }

    @Transactional
    public void deleteMealPlan(LocalDate date) {
        User currentUser = userService.getCurrentUser();
        
        MealPlan mealPlan = mealPlanRepository.findByUserAndDate(currentUser, date)
                .orElseThrow(() -> new RuntimeException("해당 날짜의 식단표를 찾을 수 없습니다."));
        
        mealPlanRepository.delete(mealPlan);
    }

    private MealPlanDto convertToDto(MealPlan mealPlan) {
        return MealPlanDto.builder()
                .id(mealPlan.getId())
                .date(mealPlan.getDate())
                .breakfast(mealPlan.getBreakfast())
                .lunch(mealPlan.getLunch())
                .dinner(mealPlan.getDinner())
                .estimatedCost(mealPlan.getEstimatedCost())
                .aiGenerated(mealPlan.isAiGenerated())
                .build();
    }
} 
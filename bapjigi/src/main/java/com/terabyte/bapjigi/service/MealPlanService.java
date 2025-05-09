package com.terabyte.bapjigi.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.terabyte.bapjigi.dto.MealPlanDto;
import com.terabyte.bapjigi.model.MealPlan;
import com.terabyte.bapjigi.model.User;
import com.terabyte.bapjigi.repository.MealPlanRepository;

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

    /**
     * 예산 목표 기간 내 모든 날짜에 대한 식단표를 생성하는 메서드
     * 임시 데이터를 사용하는 버전입니다.
     * @param budgetGoalId 예산 목표 ID (실제로는 사용하지 않음)
     * @param mealsPerDay 하루에 식사할 횟수
     * @return 날짜별, 식사별 식단 정보가 담긴 맵
     */
    @Transactional
    public Map<String, Object> generateAIMealPlansForBudgetGoal(Long budgetGoalId, int mealsPerDay) {
        User currentUser = userService.getCurrentUser();
        
        // 예산 목표 ID는 실제로 사용하지 않고 임시 데이터를 생성합니다
        // 임의의 기간과 예산 설정 (10일 기간, 300,000원 예산)
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(9);
        int totalBudget = 300000;
        
        // 날짜 범위에 대한 모든 식단 데이터를 저장할 맵
        Map<String, Object> result = new HashMap<>();
        
        // 임시 데이터 - 실제로는 더 다양한 식당과 메뉴, 가격 정보가 필요
        String[][] restaurants = {
                {"한식당", "낙원식당", "엄마의 밥상", "가정식백반", "화로집"},
                {"신선초밥", "스시히토츠", "장어와 초밥", "횟집", "일식당"},
                {"파스타나라", "피자월드", "라따뚜이", "미식가", "양식당"},
                {"홍콩반점", "마라탕", "북경", "중화요리", "짬뽕집"}
        };
        
        String[][] foods = {
                {"된장찌개", "김치찌개", "비빔밥", "불고기", "제육볶음", "삼겹살", "갈비탕", "감자탕", "해물파전", "떡볶이"},
                {"초밥세트", "연어초밥", "참치회", "모듬회", "장어덮밥", "우동", "돈카츠", "규동", "가츠동", "라멘"},
                {"크림파스타", "토마토파스타", "피자", "스테이크", "함박스테이크", "리조또", "샐러드", "햄버거", "샌드위치", "오믈렛"},
                {"짜장면", "짬뽕", "탕수육", "마라탕", "마라샹궈", "양장피", "깐풍기", "유린기", "훠궈", "양꼬치"}
        };
        
        Random random = new Random();
        
        // 날짜 간격 계산
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        // 총 식사 수
        int totalMeals = (int) (daysBetween * mealsPerDay);
        result.put("totalDays", daysBetween);
        result.put("mealsPerDay", mealsPerDay);
        result.put("totalMeals", totalMeals);
        result.put("startDate", startDate.toString());
        result.put("endDate", endDate.toString());
        
        // 기간 내 모든 식사에 대한 상세 정보
        Map<String, Map<String, Object>> mealDetails = new HashMap<>();
        
        // 목표 예산에서 식사당 평균 비용 계산
        int budgetPerMeal = totalBudget / totalMeals;
        
        // 예산 목표 기간 동안의 모든 날짜에 대해 식단 생성
        for (int i = 0; i < daysBetween; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            
            for (int j = 1; j <= mealsPerDay; j++) {
                // 날짜_식사번호 형식의 키 생성 (예: 2025-05-09_1)
                String mealKey = currentDate + "_" + j;
                
                // 랜덤한 식당 카테고리와 음식 선택
                int categoryIndex = random.nextInt(restaurants.length);
                String restaurant = restaurants[categoryIndex][random.nextInt(restaurants[categoryIndex].length)];
                String food = foods[categoryIndex][random.nextInt(foods[categoryIndex].length)];
                
                // 식사 비용에 약간의 변동 추가 (-20% ~ +20%)
                int mealCost = (int) (budgetPerMeal * (0.8 + (random.nextDouble() * 0.4)));
                
                // 식사 정보 생성
                Map<String, Object> mealInfo = new HashMap<>();
                mealInfo.put("식당명", restaurant);
                mealInfo.put("메뉴명", food);
                mealInfo.put("가격", mealCost);
                mealInfo.put("날짜", currentDate.toString());
                mealInfo.put("식사번호", j);
                
                mealDetails.put(mealKey, mealInfo);
            }
        }
        
        result.put("mealDetails", mealDetails);
        
        // 예산 정보 추가
        result.put("totalBudget", totalBudget);
        result.put("averageCostPerMeal", budgetPerMeal);
        
        return result;
    }

    @Transactional
    public MealPlan generateAIMealPlan(LocalDate date, int mealsPerDay) {
        User currentUser = userService.getCurrentUser();
        
        // AI로 식단표 생성 로직 (실제로는 외부 API를 호출하거나 복잡한 로직이 필요)
        // 여기서는 간단하게 샘플 데이터를 생성
        MealPlan mealPlan;
        
        // 하루 식사 수에 따라 식단 구성
        switch (mealsPerDay) {
            case 1:
                mealPlan = MealPlan.builder()
                    .user(currentUser)
                    .date(date)
                    .breakfast("AI 추천 한 끼 식단: 영양이 풍부한 그릭 요거트 보울과 통곡물 샌드위치")
                    .lunch(null)
                    .dinner(null)
                    .estimatedCost(5000)
                    .aiGenerated(true)
                    .build();
                break;
            case 2:
                mealPlan = MealPlan.builder()
                    .user(currentUser)
                    .date(date)
                    .breakfast("AI 추천 아침 식단: 오트밀과 과일")
                    .lunch(null)
                    .dinner("AI 추천 저녁 식단: 연어 스테이크와 구운 야채")
                    .estimatedCost(10000)
                    .aiGenerated(true)
                    .build();
                break;
            case 3:
            default:
                mealPlan = MealPlan.builder()
                    .user(currentUser)
                    .date(date)
                    .breakfast("AI 추천 아침 식단: 오트밀과 과일")
                    .lunch("AI 추천 점심 식단: 현미밥과 닭가슴살 샐러드")
                    .dinner("AI 추천 저녁 식단: 연어 스테이크와 구운 야채")
                    .estimatedCost(15000)
                    .aiGenerated(true)
                    .build();
                break;
        }
        
        return mealPlanRepository.save(mealPlan);
    }

    /**
     * 이전 버전의 generateAIMealPlan 메서드
     * 하위 호환성을 위해 유지하고 내부적으로 새 메서드를 호출
     */
    @Transactional
    public MealPlan generateAIMealPlan(LocalDate date) {
        return generateAIMealPlan(date, 3); // 기본적으로 하루 3끼
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

    /**
     * 특정 날짜의 식단표 조회 (API 테스트용 임시 데이터 반환)
     * @param date 조회할 날짜
     * @return 해당 날짜의 식단표 DTO
     */
    @Transactional(readOnly = true)
    public MealPlanDto getMealPlanByDate(LocalDate date) {
        User currentUser = userService.getCurrentUser();
        
        // DB에서 조회 시도
        return mealPlanRepository.findByUserAndDate(currentUser, date)
                .map(this::convertToDto)
                .orElseGet(() -> {
                    // 데이터가 없으면 임시 데이터 생성하여 반환 (API 테스트 목적)
                    Random random = new Random();
                    
                    // 임시 식당 및 메뉴 데이터
                    String[][] restaurants = {
                            {"한식당", "낙원식당", "엄마의 밥상", "가정식백반", "화로집"},
                            {"신선초밥", "스시히토츠", "장어와 초밥", "횟집", "일식당"},
                            {"파스타나라", "피자월드", "라따뚜이", "미식가", "양식당"},
                            {"홍콩반점", "마라탕", "북경", "중화요리", "짬뽕집"}
                    };
                    
                    String[][] foods = {
                            {"된장찌개", "김치찌개", "비빔밥", "불고기", "제육볶음", "삼겹살", "갈비탕", "감자탕", "해물파전", "떡볶이"},
                            {"초밥세트", "연어초밥", "참치회", "모듬회", "장어덮밥", "우동", "돈카츠", "규동", "가츠동", "라멘"},
                            {"크림파스타", "토마토파스타", "피자", "스테이크", "함박스테이크", "리조또", "샐러드", "햄버거", "샌드위치", "오믈렛"},
                            {"짜장면", "짬뽕", "탕수육", "마라탕", "마라샹궈", "양장피", "깐풍기", "유린기", "훠궈", "양꼬치"}
                    };
                    
                    // 아침, .점심, 저녁 메뉴 생성
                    int breakfastCat = random.nextInt(restaurants.length);
                    String breakfastRestaurant = restaurants[breakfastCat][random.nextInt(restaurants[breakfastCat].length)];
                    String breakfastFood = foods[breakfastCat][random.nextInt(foods[breakfastCat].length)];
                    
                    int lunchCat = random.nextInt(restaurants.length);
                    String lunchRestaurant = restaurants[lunchCat][random.nextInt(restaurants[lunchCat].length)];
                    String lunchFood = foods[lunchCat][random.nextInt(foods[lunchCat].length)];
                    
                    int dinnerCat = random.nextInt(restaurants.length);
                    String dinnerRestaurant = restaurants[dinnerCat][random.nextInt(restaurants[dinnerCat].length)];
                    String dinnerFood = foods[dinnerCat][random.nextInt(foods[dinnerCat].length)];
                    
                    // 가격 설정
                    int breakfastCost = random.nextInt(5000) + 5000;
                    int lunchCost = random.nextInt(6000) + 7000;
                    int dinnerCost = random.nextInt(8000) + 10000;
                    int totalCost = breakfastCost + lunchCost + dinnerCost;
                    
                    // 임시 식단 DTO 생성
                    return MealPlanDto.builder()
                            .id(0L) // 임시 ID
                            .date(date)
                            .breakfast(breakfastRestaurant + " - " + breakfastFood + " (" + breakfastCost + "원)")
                            .lunch(lunchRestaurant + " - " + lunchFood + " (" + lunchCost + "원)")
                            .dinner(dinnerRestaurant + " - " + dinnerFood + " (" + dinnerCost + "원)")
                            .estimatedCost(totalCost)
                            .aiGenerated(true)
                            .build();
                });
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
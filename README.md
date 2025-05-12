# 밥지기 (BapJigi) - AI 식단 추천을 통한 식비 관리 서비스

"밥지기"는 AI 식단 추천을 통해 사용자의 식비 관리를 돕는 서비스입니다. 사용자가 한 달 식비 예산을 입력하면 위치 기반으로 식당들의 메뉴를 식비에 맞게 식단표를 구성하여 제공합니다. 영수증 인증을 통한 식비 사용 랭킹을 메겨 식비 예산 절감을 목표로 하는 사용자들 간의 커뮤니티를 조성합니다.

## 개발 배경 및 필요성

### 배경
- 직장인들의 일상적 메뉴 선택 스트레스 증가
- 외식 물가 상승으로 인한 식비 관리의 어려움
- 과도한 메뉴 정보로 인한 의사결정 시간 증가

### 수요
- 25~34세 직장인의 68%가 메뉴 선정에 일평균 15분 소요
- 직장인 70%가 월간 식비 예산 초과 경험
- 배달 앱 평균 검색시간 8분 30초로 시간 낭비 발생

## 목표시장 및 사업화 전략

### 타겟층 
1. 초기 타겟층: 대학생
   - 대학생들은 새로운 서비스와 기술 수용도가 높고, 온라인 및 모바일 플랫폼 활용이 활발
   - 특정 대학가 중심으로 집중적인 마케팅 및 사용자 확보 전략을 시행하여 빠른 시장 검증 및 입소문 효과 기대 가능

2. 지역 커뮤니티 기반 전국 확대
   - 서비스가 지역 단위에서 자생적으로 성장할 수 있도록 지역 커뮤니티 적극 활용
   - 각 지역에 특화된 맞춤형 서비스 제공으로 지역별 네트워크 효과 극대화
   - 특정 지역에서 검증된 모델을 기반으로 전국 단위 확대

### 사업화 전략
1. MVP(최소 기능 제품) 개발
   - 대학가 주변 데이터 기반 1차 테스팅 이후 사용자 데이터 활용해 기능 확장

2. 다양한 수익 수단
   - 식당 근처 문화 시설 제휴 통한 광고비용
   - 리워드 포인트 적립 및 이커머스 연계

## 개발 방안 및 구현 계획

1. 기초 데이터베이스 구축
   - 오픈 API 활용 음식점 정보 수집
   - 핵심 메뉴/가격 정보 DB 구축

2. 위치기반 시스템 구현
   - GPS 반경 기반 음식점 검색

3. 기본 추천 시스템 개발

4. 커뮤니티 기능 구축
   - 영수증 OCR 기반 인증
   - 랭킹 및 정보 공유 커뮤니티

5. AI 모델 접목
   - 사용자 정보 활용 맞춤형 서비스

## 기능

### 회원 관리
- 회원가입: 구글 로그인 또는 직접 아이디/비밀번호 방식
- 회원정보 수집: 이름, 나이, 생년월일, 성별, 주거지역, 직업, 월 식비, 서비스 알게된 경로
- 로그인
- 비밀번호 변경
- 프로필 관리: 프로필 이름, 프로필 사진 설정/수정

### 목표 관리
- 목표 식비/기간 설정
- 사용 식비 금액 입력/저장

### 커뮤니티
- 게시물 작성, 조회, 수정, 삭제
- 랭킹 기능: 식비 랭킹 조회

### 식단 관리
- 식단표 생성
- 식단표 보기

## 기술 스택

- 백엔드: Spring Boot 3.2.3
- 데이터베이스: MySQL
- 보안: Spring Security, JWT
- 빌드 도구: Maven
- Java 버전: Java 17

## API 명세

| 카테고리 | HTTP 메서드 | 엔드포인트 | 설명 |
|---------|------------|------------|------|
| **인증** | POST | /api/auth/register | 회원 등록 |
| | POST | /api/auth/login | 로그인 |
| | POST | /api/auth/change-password | 비밀번호 변경 |
| | POST | /api/auth/update-profile | 프로필 정보 업데이트 |
| **예산 목표** | POST | /api/budget-goals | 예산 목표 생성 |
| | GET | /api/budget-goals | 모든 예산 목표 조회 |
| | GET | /api/budget-goals/current | 현재 활성화된 예산 목표 조회 |
| | GET | /api/budget-goals/{id} | 특정 예산 목표 상세 조회 |
| | PUT | /api/budget-goals/{id} | 예산 목표 수정 |
| | DELETE | /api/budget-goals/{id} | 예산 목표 삭제 |
| **식비 지출** | POST | /api/food-expenses | 식비 지출 등록 |
| | GET | /api/food-expenses | 모든 식비 지출 조회 |
| | GET | /api/food-expenses/byDateRange | 날짜 범위로 식비 지출 조회 |
| | GET | /api/food-expenses/total | 총 식비 지출 조회 |
| | GET | /api/food-expenses/{id} | 특정 식비 지출 상세 조회 |
| | PUT | /api/food-expenses/{id} | 식비 지출 수정 |
| | DELETE | /api/food-expenses/{id} | 식비 지출 삭제 |
| **게시물** | POST | /api/posts | 게시물 작성 |
| | GET | /api/posts | 모든 게시물 조회 |
| | GET | /api/posts/my-posts | 나의 게시물 조회 |
| | GET | /api/posts/search | 게시물 검색 |
| | GET | /api/posts/{id} | 특정 게시물 상세 조회 |
| | PUT | /api/posts/{id} | 게시물 수정 |
| | DELETE | /api/posts/{id} | 게시물 삭제 |
| | POST | /api/posts/{id}/like | 게시물 좋아요 |
| **식단표** | POST | /api/meal-plans | 식단표 생성 |
| | POST | /api/meal-plans/generate | AI로 식단표 생성 |
| | GET | /api/meal-plans | 모든 식단표 조회 |
| | GET | /api/meal-plans/byDateRange | 날짜 범위로 식단표 조회 |
| | GET | /api/meal-plans/byDate | 특정 날짜의 식단표 조회 |
| | PUT | /api/meal-plans/byDate | 식단표 수정 |
| | DELETE | /api/meal-plans/byDate | 식단표 삭제 |
| **랭킹** | GET | /api/ranking/food-expense | 식비 랭킹 조회 |
| | GET | /api/ranking/food-expense/period | 특정 기간의 식비 랭킹 조회 |
| | GET | /api/ranking/my-ranking | 나의 랭킹 정보 조회 |

## 설치 및 실행 방법

1. JDK 17 이상 설치
2. Maven 설치
3. MySQL 설치 및 데이터베이스 생성
4. `application.properties` 파일에서 데이터베이스 연결 정보 설정
5. 다음 명령어로 애플리케이션 실행:
   ```
   mvn spring-boot:run
   ```

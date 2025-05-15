# 밥지기 (BapJigi) - AI 식단 추천을 통한 식비 관리 서비스

"밥지기"는 AI 식단 추천을 통해 사용자의 식비 관리를 돕는 서비스입니다.

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
테스트 테스트 123
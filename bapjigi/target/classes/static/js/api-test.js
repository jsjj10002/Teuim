document.addEventListener('DOMContentLoaded', function() {
    // 토큰 저장
    let authToken = '';
    
    // 토큰 설정 함수
    function setAuthToken(token) {
        authToken = token;
        document.getElementById('authToken').value = token;
    }

    // API 결과 출력 함수
    function displayResult(elementId, result, isSuccess = true) {
        const element = document.getElementById(elementId);
        if (typeof result === 'object') {
            result = JSON.stringify(result, null, 2);
        }
        
        if (isSuccess) {
            element.innerHTML = `<div class="success-message">요청 성공!</div><pre>${result}</pre>`;
        } else {
            element.innerHTML = `<div class="error-message">요청 실패!</div><pre>${result}</pre>`;
        }
    }

    // API 호출 함수
    async function callApi(endpoint, method, data = null, needsAuth = false) {
        try {
            const headers = {
                'Content-Type': 'application/json'
            };

            if (needsAuth && authToken) {
                headers['Authorization'] = `Bearer ${authToken}`;
            }

            const fetchOptions = {
                method: method,
                headers: headers
            };

            if (data && (method === 'POST' || method === 'PUT')) {
                fetchOptions.body = JSON.stringify(data);
            }

            const response = await fetch(endpoint, fetchOptions);
            
            // 응답 형식 확인
            const contentType = response.headers.get('content-type');
            let responseData;
            
            if (contentType && contentType.includes('application/json')) {
                // JSON 형식인 경우
                responseData = await response.json();
            } else {
                // 일반 텍스트인 경우
                responseData = await response.text();
            }

            if (!response.ok) {
                throw new Error(typeof responseData === 'object' ? JSON.stringify(responseData) : responseData);
            }

            return {
                success: true,
                data: responseData
            };
        } catch (error) {
            console.error('API 호출 오류:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    // 회원가입 API
    document.getElementById('registerBtn').addEventListener('click', async function() {
        const name = document.getElementById('registerName').value;
        const username = document.getElementById('registerUsername').value;
        const email = document.getElementById('registerEmail').value;
        const password = document.getElementById('registerPassword').value;
        
        if (!name || !username || !email || !password) {
            displayResult('registerResult', '모든 필드를 입력해주세요.', false);
            return;
        }
        
        const data = {
            name: name,
            username: username,
            email: email,
            password: password
        };
        
        const result = await callApi('/api/auth/register', 'POST', data);
        
        displayResult('registerResult', result.success ? result.data : result.error, result.success);
    });

    // 로그인 API
    document.getElementById('loginBtn').addEventListener('click', async function() {
        const username = document.getElementById('loginUsername').value;
        const password = document.getElementById('loginPassword').value;
        
        if (!username || !password) {
            displayResult('loginResult', '사용자 이름과 비밀번호를 입력해주세요.', false);
            return;
        }
        
        const data = {
            username: username,
            password: password
        };
        
        const result = await callApi('/api/auth/login', 'POST', data);
        
        displayResult('loginResult', result.success ? result.data : result.error, result.success);
        
        if (result.success && result.data.token) {
            setAuthToken(result.data.token);
        }
    });

    // 비밀번호 변경 API
    document.getElementById('changePasswordBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('changePasswordResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const oldPassword = document.getElementById('oldPassword').value;
        const newPassword = document.getElementById('newPassword').value;
        
        if (!oldPassword || !newPassword) {
            displayResult('changePasswordResult', '현재 비밀번호와 새 비밀번호를 입력해주세요.', false);
            return;
        }
        
        // URL 쿼리 파라미터로 변경
        const result = await callApi(`/api/auth/change-password?oldPassword=${encodeURIComponent(oldPassword)}&newPassword=${encodeURIComponent(newPassword)}`, 'POST', null, true);
        
        displayResult('changePasswordResult', result.success ? result.data : result.error, result.success);
    });

    // 프로필 업데이트 API
    document.getElementById('updateProfileBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('updateProfileResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const name = document.getElementById('profileName').value;
        const imageUrl = document.getElementById('profileImage').value;
        
        if (!name) {
            displayResult('updateProfileResult', '프로필 이름은 필수 입력 항목입니다.', false);
            return;
        }
        
        const data = {
            name: name,
            imageUrl: imageUrl || null
        };
        
        const result = await callApi('/api/auth/update-profile', 'POST', data, true);
        
        displayResult('updateProfileResult', result.success ? result.data : result.error, result.success);
    });

    // 예산 목표 생성 API
    document.getElementById('createBudgetBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('createBudgetResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const amount = document.getElementById('budgetAmount').value;
        const startDate = document.getElementById('budgetStartDate').value;
        const endDate = document.getElementById('budgetEndDate').value;
        
        if (!amount || !startDate || !endDate) {
            displayResult('createBudgetResult', '모든 필드를 입력해주세요.', false);
            return;
        }
        
        const data = {
            targetAmount: parseFloat(amount),
            startDate: startDate,
            endDate: endDate
        };
        
        const result = await callApi('/api/budget-goals', 'POST', data, true);
        
        displayResult('createBudgetResult', result.success ? result.data : result.error, result.success);
    });

    // 예산 목표 조회 API
    document.getElementById('getBudgetsBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('getBudgetsResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const result = await callApi('/api/budget-goals', 'GET', null, true);
        
        displayResult('getBudgetsResult', result.success ? result.data : result.error, result.success);
    });

    // 현재 예산 목표 조회 API
    document.getElementById('getCurrentBudgetBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('getCurrentBudgetResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const result = await callApi('/api/budget-goals/current', 'GET', null, true);
        
        displayResult('getCurrentBudgetResult', result.success ? result.data : result.error, result.success);
    });

    // 특정 예산 목표 조회 API
    document.getElementById('getBudgetByIdBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('getBudgetByIdResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const budgetId = document.getElementById('budgetIdToGet').value;
        
        if (!budgetId) {
            displayResult('getBudgetByIdResult', '예산 목표 ID를 입력해주세요.', false);
            return;
        }
        
        const result = await callApi(`/api/budget-goals/${budgetId}`, 'GET', null, true);
        
        displayResult('getBudgetByIdResult', result.success ? result.data : result.error, result.success);
    });

    // 예산 목표 수정 API
    document.getElementById('updateBudgetBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('updateBudgetResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const budgetId = document.getElementById('budgetIdToUpdate').value;
        const amount = document.getElementById('updateBudgetAmount').value;
        const startDate = document.getElementById('updateBudgetStartDate').value;
        const endDate = document.getElementById('updateBudgetEndDate').value;
        
        if (!budgetId || !amount || !startDate || !endDate) {
            displayResult('updateBudgetResult', '모든 필드를 입력해주세요.', false);
            return;
        }
        
        const data = {
            targetAmount: parseFloat(amount),
            startDate: startDate,
            endDate: endDate
        };
        
        const result = await callApi(`/api/budget-goals/${budgetId}`, 'PUT', data, true);
        
        displayResult('updateBudgetResult', result.success ? result.data : result.error, result.success);
    });

    // 예산 목표 삭제 API
    document.getElementById('deleteBudgetBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('deleteBudgetResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const budgetId = document.getElementById('budgetIdToDelete').value;
        
        if (!budgetId) {
            displayResult('deleteBudgetResult', '예산 목표 ID를 입력해주세요.', false);
            return;
        }

        // 삭제 전 확인 메시지 표시
        if (!confirm('이 예산 목표를 삭제하면 관련된 모든 식비 지출 기록도 함께 삭제해야 합니다. 먼저 이 예산 목표와 연결된 식비 지출 기록을 삭제해주세요. 계속 진행하시겠습니까?')) {
            return;
        }
        
        const result = await callApi(`/api/budget-goals/${budgetId}`, 'DELETE', null, true);
        
        if (!result.success && result.error.includes('foreign key constraint fails')) {
            displayResult('deleteBudgetResult', '이 예산 목표는 식비 지출 기록에서 참조하고 있어 삭제할 수 없습니다. 먼저 관련된 식비 지출 기록을 삭제해주세요.', false);
            return;
        }
        
        displayResult('deleteBudgetResult', result.success ? result.data : result.error, result.success);
    });

    // 식비 지출 등록 API
    document.getElementById('createExpenseBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('createExpenseResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const amount = document.getElementById('expenseAmount').value;
        const date = document.getElementById('expenseDate').value;
        const description = document.getElementById('expenseDescription').value;
        
        if (!amount || !date) {
            displayResult('createExpenseResult', '금액과 날짜는 필수 입력 항목입니다.', false);
            return;
        }
        
        const data = {
            amount: parseFloat(amount),
            date: date,
            description: description
        };
        
        const result = await callApi('/api/food-expenses', 'POST', data, true);
        
        displayResult('createExpenseResult', result.success ? result.data : result.error, result.success);
    });

    // 식비 지출 조회 API
    document.getElementById('getExpensesBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('getExpensesResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const result = await callApi('/api/food-expenses', 'GET', null, true);
        
        displayResult('getExpensesResult', result.success ? result.data : result.error, result.success);
    });
    
    // 날짜 범위별 식비 지출 조회 API
    document.getElementById('getExpensesByDateRangeBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('getExpensesByDateRangeResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const startDate = document.getElementById('expenseStartDate').value;
        const endDate = document.getElementById('expenseEndDate').value;
        
        if (!startDate || !endDate) {
            displayResult('getExpensesByDateRangeResult', '시작일과 종료일을 입력해주세요.', false);
            return;
        }
        
        const result = await callApi(`/api/food-expenses/byDateRange?startDate=${startDate}&endDate=${endDate}`, 'GET', null, true);
        
        displayResult('getExpensesByDateRangeResult', result.success ? result.data : result.error, result.success);
    });
    
    // 총 식비 지출 조회 API
    document.getElementById('getTotalExpenseBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('getTotalExpenseResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const startDate = document.getElementById('totalExpenseStartDate').value;
        const endDate = document.getElementById('totalExpenseEndDate').value;
        
        if (!startDate || !endDate) {
            displayResult('getTotalExpenseResult', '시작일과 종료일을 입력해주세요.', false);
            return;
        }
        
        const result = await callApi(`/api/food-expenses/total?startDate=${startDate}&endDate=${endDate}`, 'GET', null, true);
        
        displayResult('getTotalExpenseResult', result.success ? result.data : result.error, result.success);
    });
    
    // 특정 식비 지출 조회 API
    document.getElementById('getExpenseByIdBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('getExpenseByIdResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const expenseId = document.getElementById('expenseIdToGet').value;
        
        if (!expenseId) {
            displayResult('getExpenseByIdResult', '식비 지출 ID를 입력해주세요.', false);
            return;
        }
        
        const result = await callApi(`/api/food-expenses/${expenseId}`, 'GET', null, true);
        
        displayResult('getExpenseByIdResult', result.success ? result.data : result.error, result.success);
    });
    
    // 식비 지출 수정 API
    document.getElementById('updateExpenseBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('updateExpenseResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const expenseId = document.getElementById('expenseIdToUpdate').value;
        const amount = document.getElementById('updateExpenseAmount').value;
        const date = document.getElementById('updateExpenseDate').value;
        const description = document.getElementById('updateExpenseDescription').value;
        
        if (!expenseId || !amount || !date) {
            displayResult('updateExpenseResult', '식비 지출 ID, 금액, 날짜는 필수 입력 항목입니다.', false);
            return;
        }
        
        const data = {
            amount: parseFloat(amount),
            date: date,
            description: description
        };
        
        const result = await callApi(`/api/food-expenses/${expenseId}`, 'PUT', data, true);
        
        displayResult('updateExpenseResult', result.success ? result.data : result.error, result.success);
    });
    
    // 식비 지출 삭제 API
    document.getElementById('deleteExpenseBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('deleteExpenseResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const expenseId = document.getElementById('expenseIdToDelete').value;
        
        if (!expenseId) {
            displayResult('deleteExpenseResult', '식비 지출 ID를 입력해주세요.', false);
            return;
        }
        
        const result = await callApi(`/api/food-expenses/${expenseId}`, 'DELETE', null, true);
        
        displayResult('deleteExpenseResult', result.success ? result.data : result.error, result.success);
    });

    // AI 식단표 생성 API
    document.getElementById('generateMealPlanBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('generateMealPlanResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const budgetGoalId = document.getElementById('budgetGoalId').value;
        const mealsPerDay = document.getElementById('mealsPerDay').value;
        
        if (!budgetGoalId) {
            displayResult('generateMealPlanResult', '예산 목표 ID를 입력해주세요. "모든 예산 목표 조회" 버튼을 클릭하여 ID를 확인할 수 있습니다.', false);
            return;
        }
        
        // API URL에 쿼리 파라미터 추가
        const url = `/api/meal-plans/generate?budgetGoalId=${budgetGoalId}&mealsPerDay=${mealsPerDay}`;
        
        const result = await callApi(url, 'POST', null, true);
        
        displayResult('generateMealPlanResult', result.success ? result.data : result.error, result.success);
    });

    // 식단표 조회 API
    document.getElementById('getMealPlanBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('getMealPlanResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const date = document.getElementById('mealPlanDate').value;
        
        if (!date) {
            displayResult('getMealPlanResult', '날짜를 입력해주세요.', false);
            return;
        }
        
        const result = await callApi(`/api/meal-plans/byDate?date=${date}`, 'GET', null, true);
        
        displayResult('getMealPlanResult', result.success ? result.data : result.error, result.success);
    });
    
    // 모든 식단표 조회 API
    document.getElementById('getAllMealPlansBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('getAllMealPlansResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const result = await callApi('/api/meal-plans', 'GET', null, true);
        
        displayResult('getAllMealPlansResult', result.success ? result.data : result.error, result.success);
    });
    
    // 날짜 범위별 식단표 조회 API
    document.getElementById('getMealPlansByDateRangeBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('getMealPlansByDateRangeResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const startDate = document.getElementById('mealPlanStartDate').value;
        const endDate = document.getElementById('mealPlanEndDate').value;
        
        if (!startDate || !endDate) {
            displayResult('getMealPlansByDateRangeResult', '시작일과 종료일을 입력해주세요.', false);
            return;
        }
        
        const result = await callApi(`/api/meal-plans/byDateRange?startDate=${startDate}&endDate=${endDate}`, 'GET', null, true);
        
        displayResult('getMealPlansByDateRangeResult', result.success ? result.data : result.error, result.success);
    });
    
    // 식단표 수정 API
    document.getElementById('updateMealPlanBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('updateMealPlanResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const date = document.getElementById('updateMealPlanDate').value;
        const breakfast = document.getElementById('updateMealPlanBreakfast').value;
        const lunch = document.getElementById('updateMealPlanLunch').value;
        const dinner = document.getElementById('updateMealPlanDinner').value;
        const cost = document.getElementById('updateMealPlanCost').value;
        
        if (!date) {
            displayResult('updateMealPlanResult', '날짜는 필수 입력 항목입니다.', false);
            return;
        }
        
        const data = {
            date: date,
            breakfast: breakfast,
            lunch: lunch,
            dinner: dinner,
            estimatedCost: parseFloat(cost) || 0
        };
        
        const result = await callApi(`/api/meal-plans/byDate?date=${date}`, 'PUT', data, true);
        
        displayResult('updateMealPlanResult', result.success ? result.data : result.error, result.success);
    });
    
    // 식단표 삭제 API
    document.getElementById('deleteMealPlanBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('deleteMealPlanResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const date = document.getElementById('deleteMealPlanDate').value;
        
        if (!date) {
            displayResult('deleteMealPlanResult', '날짜를 입력해주세요.', false);
            return;
        }
        
        const result = await callApi(`/api/meal-plans/byDate?date=${date}`, 'DELETE', null, true);
        
        displayResult('deleteMealPlanResult', result.success ? result.data : result.error, result.success);
    });

    // 게시물 작성 API
    document.getElementById('createPostBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('createPostResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const title = document.getElementById('postTitle').value;
        const content = document.getElementById('postContent').value;
        
        if (!title || !content) {
            displayResult('createPostResult', '제목과 내용은 필수 입력 항목입니다.', false);
            return;
        }
        
        const data = {
            title: title,
            content: content
        };
        
        const result = await callApi('/api/posts', 'POST', data, true);
        
        displayResult('createPostResult', result.success ? result.data : result.error, result.success);
    });
    
    // 모든 게시물 조회 API
    document.getElementById('getAllPostsBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('getAllPostsResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const page = document.getElementById('postPage').value;
        const size = document.getElementById('postSize').value;
        
        const result = await callApi(`/api/posts?page=${page}&size=${size}`, 'GET', null, true);
        
        displayResult('getAllPostsResult', result.success ? result.data : result.error, result.success);
    });
    
    // 내 게시물 조회 API
    document.getElementById('getMyPostsBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('getMyPostsResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const result = await callApi('/api/posts/my-posts', 'GET', null, true);
        
        displayResult('getMyPostsResult', result.success ? result.data : result.error, result.success);
    });
    
    // 게시물 검색 API
    document.getElementById('searchPostsBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('searchPostsResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const keyword = document.getElementById('postKeyword').value;
        const page = document.getElementById('searchPostPage').value;
        const size = document.getElementById('searchPostSize').value;
        
        if (!keyword) {
            displayResult('searchPostsResult', '검색어를 입력해주세요.', false);
            return;
        }
        
        const result = await callApi(`/api/posts/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`, 'GET', null, true);
        
        displayResult('searchPostsResult', result.success ? result.data : result.error, result.success);
    });
    
    // 특정 게시물 조회 API
    document.getElementById('getPostByIdBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('getPostByIdResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const postId = document.getElementById('postIdToGet').value;
        
        if (!postId) {
            displayResult('getPostByIdResult', '게시물 ID를 입력해주세요.', false);
            return;
        }
        
        const result = await callApi(`/api/posts/${postId}`, 'GET', null, true);
        
        displayResult('getPostByIdResult', result.success ? result.data : result.error, result.success);
    });
    
    // 게시물 수정 API
    document.getElementById('updatePostBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('updatePostResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const postId = document.getElementById('postIdToUpdate').value;
        const title = document.getElementById('updatePostTitle').value;
        const content = document.getElementById('updatePostContent').value;
        
        if (!postId || !title || !content) {
            displayResult('updatePostResult', '게시물 ID, 제목, 내용은 필수 입력 항목입니다.', false);
            return;
        }
        
        const data = {
            title: title,
            content: content
        };
        
        const result = await callApi(`/api/posts/${postId}`, 'PUT', data, true);
        
        displayResult('updatePostResult', result.success ? result.data : result.error, result.success);
    });
    
    // 게시물 삭제 API
    document.getElementById('deletePostBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('deletePostResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const postId = document.getElementById('postIdToDelete').value;
        
        if (!postId) {
            displayResult('deletePostResult', '게시물 ID를 입력해주세요.', false);
            return;
        }
        
        const result = await callApi(`/api/posts/${postId}`, 'DELETE', null, true);
        
        displayResult('deletePostResult', result.success ? result.data : result.error, result.success);
    });
    
    // 게시물 좋아요 API
    document.getElementById('likePostBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('likePostResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const postId = document.getElementById('postIdToLike').value;
        
        if (!postId) {
            displayResult('likePostResult', '게시물 ID를 입력해주세요.', false);
            return;
        }
        
        const result = await callApi(`/api/posts/${postId}/like`, 'POST', null, true);
        
        displayResult('likePostResult', result.success ? result.data : result.error, result.success);
    });
    
    // 식비 랭킹 조회 API
    document.getElementById('getFoodExpenseRankingBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('getFoodExpenseRankingResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const result = await callApi('/api/ranking/food-expense', 'GET', null, true);
        
        displayResult('getFoodExpenseRankingResult', result.success ? result.data : result.error, result.success);
    });
    
    // 특정 기간 식비 랭킹 조회 API
    document.getElementById('getFoodExpenseRankingByPeriodBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('getFoodExpenseRankingByPeriodResult', '로그인이 필요합니다.', false);
            return;
        }
        
        const startDate = document.getElementById('rankingStartDate').value;
        const endDate = document.getElementById('rankingEndDate').value;
        
        if (!startDate || !endDate) {
            displayResult('getFoodExpenseRankingByPeriodResult', '시작일과 종료일을 입력해주세요.', false);
            return;
        }
        
        const result = await callApi(`/api/ranking/food-expense/period?startDate=${startDate}&endDate=${endDate}`, 'GET', null, true);
        
        displayResult('getFoodExpenseRankingByPeriodResult', result.success ? result.data : result.error, result.success);
    });
    
    // 나의 랭킹 정보 조회 API
    document.getElementById('getMyRankingInfoBtn').addEventListener('click', async function() {
        if (!authToken) {
            displayResult('getMyRankingInfoResult', '로그인이 필요합니다.', false);
            return;
        }
        
        // 먼저 현재 로그인한 사용자 정보 가져오기
        const userInfoResult = await callApi('/api/auth/user-info', 'GET', null, true);
        
        const result = await callApi('/api/ranking/my-ranking', 'GET', null, true);
        
        if (result.success) {
            // 현재 로그인한 사용자와 랭킹 정보의 사용자가 일치하는지 확인
            if (userInfoResult.success && userInfoResult.data.username !== result.data.username) {
                // 결과는 성공이지만 사용자 불일치 경고 추가
                displayResult(
                    'getMyRankingInfoResult', 
                    `주의: 표시된 랭킹 정보가 현재 로그인한 사용자(${userInfoResult.data.username})와 다른 사용자(${result.data.username})의 정보입니다.\n\n` + 
                    JSON.stringify(result.data, null, 2), 
                    true
                );
            } else {
                displayResult('getMyRankingInfoResult', result.data, true);
            }
        } else {
            displayResult('getMyRankingInfoResult', result.error, false);
        }
    });
}); 
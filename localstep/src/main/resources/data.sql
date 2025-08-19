-- 1. 테스트용 사용자 데이터
INSERT INTO users (id, email, password, nickname) VALUES 
(1, 'test1@example.com', 'password123', '테스트 사용자 1'),
(2, 'test2@example.com', 'password123', '테스트 사용자 2');

-- 2. 테스트용 Goal 데이터 (StepsEntity.Goal)
INSERT INTO goals (goal_id, user_id, goal_steps, set_date) VALUES 
(1, 1, 5000, CURRENT_DATE),
(2, 2, 5000, CURRENT_DATE);

-- 3. 테스트용 Route 데이터
INSERT INTO route (id, user_id, user_lat, user_lng, goal_steps, completed, created_at) VALUES 
(1, 1, 37.5665, 126.9780, 5000, false, CURRENT_TIMESTAMP),
(2, 2, 37.5666, 126.9781, 5000, false, CURRENT_TIMESTAMP);

-- 4. 테스트용 Quest 데이터
INSERT INTO quest (quest_id, user_id, quest_type, goal_id, target_store_id, completed_at, reward_points) VALUES 
(1, 1, 'STEP_GOAL', 1, null, null, 500),
(2, 1, 'STORE_VISIT', null, 1, null, 100),
(3, 2, 'STEP_GOAL', 2, null, null, 500),
(4, 2, 'STORE_VISIT', null, 2, null, 100);

-- 5. 테스트용 StepRecord 데이터 (StepsEntity.StepRecord)
INSERT INTO step_records (record_id, user_id, date, current_steps) VALUES 
(1, 1, CURRENT_DATE, 6000),
(2, 2, CURRENT_DATE, 6000);

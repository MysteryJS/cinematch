-- ================================
-- TEST USERS
-- ================================
INSERT INTO users (username, email, password_hash) VALUES
('testuser1', 'user1@gmail.com', 'pass123'),
('testuser2', 'user2@gmail.com', 'abcd'),
('demouser', 'demo@gmail.com', '12345');

-- ================================
-- TEST SEARCH HISTORY
-- ================================
INSERT INTO search_history (user_id, movie_id, searched_at) VALUES
(1, 101, NOW()),
(1, 102, NOW()),
(2, 103, NOW()),
(3, 105, NOW());

-- ================================
-- TEST FAVORITES
-- ================================
INSERT INTO favorites (user_id, movie_id) VALUES
(1, 101),
(1, 102),
(2, 103);

-- ================================
-- TEST QUIZ HISTORY
-- ================================
INSERT INTO quiz_history (user_id, score) VALUES
(1, 8),
(1, 10),
(2, 5),
(3, 9);

-- ================================
-- TEST KPIS
-- ================================
INSERT INTO kpis (target_type, target_id, audience_engagement, star_power, awards_potential, popularity_proxy)
VALUES
('movie', 101, 0.85, 0.78, 0.6, 0.9),
('movie', 102, 0.75, 0.55, 0.4, 0.8),
('actor', 201, 0.90, 0.95, 0.85, 0.88),
('director', 301, 0.88, 0.92, 0.8, 0.89);

-- ================================================================
-- SEED DATA: DASHBOARD & CHARTS (Safe Version)
-- Sử dụng User có sẵn (ID 4, 5) và Tour có sẵn (1-20)
-- ================================================================

-- 1. DỮ LIỆU USER MỚI (Để test KPI: User Growth)
-- Tạo 3 user mới đăng ký trong 7 ngày qua (ID 101-103 tránh trùng ID cũ)
INSERT IGNORE INTO users (id, email, password, provider, status, created_at) VALUES 
(101, 'new_user_today@test.com', '$2y$10$spT5sG1xCyizYdxSF2/gBeUKKA4EsTM3zEL9aKD6GfZ.IGPk0tjkq', 'LOCAL', 'ACTIVE', NOW()), 
(102, 'new_user_yesterday@test.com', '$2y$10$spT5sG1xCyizYdxSF2/gBeUKKA4EsTM3zEL9aKD6GfZ.IGPk0tjkq', 'LOCAL', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(103, 'new_user_weekago@test.com', '$2y$10$spT5sG1xCyizYdxSF2/gBeUKKA4EsTM3zEL9aKD6GfZ.IGPk0tjkq', 'LOCAL', 'UNVERIFIED', DATE_SUB(NOW(), INTERVAL 5 DAY));

INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (101, 2), (102, 2), (103, 2);

INSERT IGNORE INTO profiles (user_id, full_name, avatar_url) VALUES 
(101, 'New User A', 'https://ui-avatars.com/api/?name=User+A'),
(102, 'New User B', 'https://ui-avatars.com/api/?name=User+B'),
(103, 'New User C', 'https://ui-avatars.com/api/?name=User+C');


-- 2. DỮ LIỆU BOOKING & DOANH THU (Rải rác 12 tháng năm 2025)
-- Chỉ dùng User ID 4 (Thạch), 5 (Lộc) và 1 (Admin)

-- Tháng 1: User 4 đi Tour 1 (Phú Quốc) - PAID (7tr)
INSERT INTO bookings (user_id, tour_id, booking_date, start_date, num_people, total_price, status, payment_method)
VALUES (4, 1, '2025-01-15 08:00:00', '2025-01-20', 2, 7000000, 'PAID', 'BANKING');
INSERT INTO payments (booking_id, amount, payment_date, payment_status) 
SELECT id, 7000000, '2025-01-15 08:05:00', 'SUCCESS' FROM bookings WHERE booking_date = '2025-01-15 08:00:00';

-- Tháng 2: User 5 đi Tour 2 (Đà Nẵng) - CANCELLED (Không tính doanh thu)
INSERT INTO bookings (user_id, tour_id, booking_date, start_date, num_people, total_price, status, payment_method)
VALUES (5, 2, '2025-02-10 09:30:00', '2025-02-25', 2, 15000000, 'CANCELLED', 'BANKING');

-- Tháng 3: User 4 đi Tour 7 (Huế) - PAID (8.4tr)
INSERT INTO bookings (user_id, tour_id, booking_date, start_date, num_people, total_price, status, payment_method)
VALUES (4, 7, '2025-03-05 14:00:00', '2025-03-10', 3, 8400000, 'PAID', 'CASH');
INSERT INTO payments (booking_id, amount, payment_date, payment_status) 
SELECT id, 8400000, '2025-03-05 14:00:00', 'SUCCESS' FROM bookings WHERE booking_date = '2025-03-05 14:00:00';

-- Tháng 4: User 5 đi Tour 14 (Thái Lan) - PAID (17tr)
INSERT INTO bookings (user_id, tour_id, booking_date, start_date, num_people, total_price, status, payment_method)
VALUES (5, 14, '2025-04-01 10:00:00', '2025-04-28', 2, 17000000, 'PAID', 'BANKING');
INSERT INTO payments (booking_id, amount, payment_date, payment_status) 
SELECT id, 17000000, '2025-04-01 10:15:00', 'SUCCESS' FROM bookings WHERE booking_date = '2025-04-01 10:00:00';

-- Tháng 5: User 1 đi Tour 15 (Sing) - CONFIRMED (Chưa thanh toán)
INSERT INTO bookings (user_id, tour_id, booking_date, start_date, num_people, total_price, status, payment_method)
VALUES (1, 15, '2025-05-15 11:00:00', '2025-06-01', 3, 34500000, 'CONFIRMED', 'BANKING');

-- Tháng 6: User 5 đi Tour 5 (Lý Sơn) - PAID (39tr)
INSERT INTO bookings (user_id, tour_id, booking_date, start_date, num_people, total_price, status, payment_method)
VALUES (5, 5, '2025-06-10 08:00:00', '2025-06-20', 10, 39000000, 'PAID', 'BANKING');
INSERT INTO payments (booking_id, amount, payment_date, payment_status) 
SELECT id, 39000000, '2025-06-10 08:30:00', 'SUCCESS' FROM bookings WHERE booking_date = '2025-06-10 08:00:00';

-- Tháng 7: User 4 đặt nhiều tour Hè (PAID)
INSERT INTO bookings (user_id, tour_id, booking_date, start_date, num_people, total_price, status, payment_method) VALUES 
(4, 3, '2025-07-01 09:00:00', '2025-07-05', 2, 5200000, 'PAID', 'BANKING'),
(4, 4, '2025-07-02 10:00:00', '2025-07-10', 5, 16000000, 'PAID', 'BANKING'),
(4, 12, '2025-07-05 11:00:00', '2025-07-15', 2, 13800000, 'PAID', 'CASH');

INSERT INTO payments (booking_id, amount, payment_date, payment_status)
SELECT id, total_price, booking_date, 'SUCCESS' FROM bookings WHERE MONTH(booking_date) = 7 AND YEAR(booking_date) = 2025;

-- Tháng 8: User 5 đi Hàn Quốc (Tour 16) - PAID (66tr)
INSERT INTO bookings (user_id, tour_id, booking_date, start_date, num_people, total_price, status, payment_method)
VALUES (5, 16, '2025-08-01 09:00:00', '2025-08-20', 4, 66000000, 'PAID', 'BANKING');
INSERT INTO payments (booking_id, amount, payment_date, payment_status) 
SELECT id, 66000000, '2025-08-01 09:10:00', 'SUCCESS' FROM bookings WHERE booking_date = '2025-08-01 09:00:00';

-- Tháng 9, 10, 11: Mùa thu (Lai rai)
INSERT INTO bookings (user_id, tour_id, booking_date, start_date, num_people, total_price, status, payment_method) VALUES 
(5, 9, '2025-09-05 08:00:00', '2025-09-15', 6, 25200000, 'PAID', 'BANKING'), -- Tà Năng
(4, 10, '2025-10-10 09:00:00', '2025-10-12', 2, 2400000, 'PENDING', 'BANKING'), -- Bà Đen (Chưa trả)
(4, 18, '2025-11-15 10:00:00', '2025-11-20', 2, 3600000, 'PAID', 'CASH'); -- Cúc Phương

INSERT INTO payments (booking_id, amount, payment_date, payment_status)
SELECT id, total_price, booking_date, 'SUCCESS' FROM bookings WHERE MONTH(booking_date) IN (9, 11) AND status = 'PAID' AND YEAR(booking_date) = 2025;


-- 3. DỮ LIỆU "HÔM NAY" (Quan trọng cho KPI Dashboard)
-- Sử dụng hàm NOW() để luôn đúng với thời điểm chạy
INSERT INTO bookings (user_id, tour_id, booking_date, start_date, num_people, total_price, status, payment_method) VALUES 
(4, 19, NOW(), DATE_ADD(NOW(), INTERVAL 2 DAY), 2, 2400000, 'PENDING', 'CASH'),
(5, 20, NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY), 2, 2000000, 'PAID', 'BANKING'),
(1, 11, NOW(), DATE_ADD(NOW(), INTERVAL 5 DAY), 4, 14000000, 'CONFIRMED', 'BANKING');

INSERT INTO payments (booking_id, amount, payment_date, payment_status)
SELECT id, total_price, booking_date, 'SUCCESS' FROM bookings WHERE id IN (SELECT id FROM bookings WHERE DATE(booking_date) = DATE(NOW()) AND status = 'PAID');
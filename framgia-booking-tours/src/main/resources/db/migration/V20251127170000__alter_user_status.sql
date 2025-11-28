-- Bước 1: Mở rộng ENUM để chứa cả giá trị cũ ('INACTIVE') và mới ('UNVERIFIED', 'BLOCKED')
-- Mục đích: Để tránh lỗi data truncation khi update dữ liệu
ALTER TABLE users MODIFY COLUMN status ENUM('ACTIVE', 'INACTIVE', 'UNVERIFIED', 'BLOCKED') DEFAULT 'ACTIVE';

-- Bước 2: Migrate dữ liệu cũ
-- Giả sử các user 'INACTIVE' cũ là user bị khóa (BLOCKED).
-- Nếu ý định cũ của bạn INACTIVE là chưa kích hoạt thì đổi thành 'UNVERIFIED'
UPDATE users SET status = 'BLOCKED' WHERE status = 'INACTIVE';

-- Bước 3: Chốt lại ENUM mới và set default là UNVERIFIED (User mới tạo phải verify email)
ALTER TABLE users MODIFY COLUMN status ENUM('UNVERIFIED', 'ACTIVE', 'BLOCKED') DEFAULT 'UNVERIFIED';
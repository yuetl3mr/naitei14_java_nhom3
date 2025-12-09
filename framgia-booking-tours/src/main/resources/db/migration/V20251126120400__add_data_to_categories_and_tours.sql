INSERT IGNORE INTO categories (id, name, description) VALUES
(1, 'Du lịch Biển Đảo', 'Các tour nghỉ dưỡng, lặn biển và khám phá đảo.'),
(2, 'Khám phá Văn Hóa', 'Các tour di sản, đền đài và tìm hiểu lịch sử.'),
(3, 'Tour Mạo Hiểm', 'Leo núi, trekking, cắm trại và các hoạt động mạnh.'),
(4, 'Nghỉ dưỡng Cao cấp', 'Các resort, spa và dịch vụ 5 sao.'),
(5, 'Du lịch Nước ngoài', 'Các chuyến đi Châu Âu, Châu Á, Mỹ.'),
(6, 'Du lịch Sinh thái', 'Các tour gần gũi thiên nhiên, rừng quốc gia, bảo tồn.'),
(7, 'Ẩm thực & Trải nghiệm địa phương', 'Khám phá món ăn bản địa, chợ đêm và trải nghiệm truyền thống.');

INSERT IGNORE INTO tours (id, category_id, creator_id, name, description, location, price, duration_days, available_slots, image_url, status) VALUES

-- Biển Đảo
(1, 1, 2, 'Phú Quốc - Thiên Đường Biển Xanh', 'Khám phá 4 đảo ngọc, lặn ngắm san hô.', 'Kiên Giang, Việt Nam', 3500000.00, 3, 50, 'https://ik.imagekit.io/28926333pl/tours/phuquoc_thumb.jpg', 'AVAILABLE'),
(2, 1, 2, 'Đà Nẵng - Bán Đảo Sơn Trà Resort', 'Nghỉ dưỡng tại resort 5 sao ven biển.', 'Đà Nẵng, Việt Nam', 7500000.00, 4, 30, 'https://ik.imagekit.io/28926333pl/tours/danang_resort.jpg', 'AVAILABLE'),
(3, 1, 2, 'Nha Trang – Vịnh San Hô', 'Lặn biển, đi cano và tham quan Hòn Mun.', 'Khánh Hòa, Việt Nam', 2600000.00, 2, 40, 'https://ik.imagekit.io/28926333pl/tours/nhatrang_coral_bay.jpg', 'AVAILABLE'),
(4, 1, 2, 'Cô Tô – Thiên đường hoang sơ', 'Bãi biển xanh, trải nghiệm cắm trại ven biển.', 'Quảng Ninh, Việt Nam', 3200000.00, 3, 25, 'https://ik.imagekit.io/28926333pl/tours/coto_island.jpg', 'AVAILABLE'),
(5, 1, 2, 'Lý Sơn – Vương quốc tỏi & biển xanh', 'Khám phá hang Cau, đỉnh Thới Lới và lặn san hô.', 'Quảng Ngãi, Việt Nam', 3900000.00, 3, 40, 'https://ik.imagekit.io/28926333pl/tours/lyson_island.jpg', 'AVAILABLE'),
(6, 1, 2, 'Phan Thiết – Mũi Né Cát Trắng', 'Khám phá đồi cát, lướt ván diều và trải nghiệm làng chài.', 'Bình Thuận, Việt Nam', 2800000.00, 2, 35, 'https://ik.imagekit.io/28926333pl/tours/mui_ne_white_sand.jpg', 'AVAILABLE'),

-- Văn Hóa
(7, 2, 2, 'Huế – Hành trình di sản', 'Đại Nội, chùa Thiên Mụ, lăng Tự Đức.', 'Thừa Thiên – Huế, Việt Nam', 2800000.00, 3, 50, 'https://ik.imagekit.io/28926333pl/tours/hue_di_san.jpg', 'AVAILABLE'),
(8, 2, 2, 'Hội An Phố Cổ', 'Dạo phố đèn lồng, ngắm hoài phố cổ.', 'Quảng Nam, Việt Nam', 1500000.00, 2, 80, 'https://ik.imagekit.io/28926333pl/tours/hoian_phoco.jpg', 'AVAILABLE'),

-- Mạo Hiểm
(9, 3, 2, 'Trekking Tà Năng – Phan Dũng', 'Cung trekking đẹp nhất Việt Nam.', 'Lâm Đồng – Bình Thuận', 4200000.00, 3, 0, 'https://ik.imagekit.io/28926333pl/tours/ta_nang_phan_dung.jpg', 'UNAVAILABLE'),
(10, 3, 2, 'Chinh phục đỉnh Bà Đen', 'Leo núi và cáp treo.', 'Tây Ninh, Việt Nam', 1200000.00, 1, 60, 'https://ik.imagekit.io/28926333pl/tours/ba_den_peak.jpg', 'AVAILABLE'),
(11, 3, 2, 'Rafting Đà Lạt', 'Trải nghiệm chèo thuyền vượt thác.', 'Lâm Đồng, Việt Nam', 3500000.00, 2, 20, 'https://ik.imagekit.io/28926333pl/tours/dalat_rafting.jpg', 'AVAILABLE'),

-- Nghỉ dưỡng cao cấp
(12, 4, 2, 'Vinpearl Nha Trang Resort 5⭐', 'Nghỉ dưỡng + vui chơi VinWonders.', 'Nha Trang, Việt Nam', 6900000.00, 3, 35, 'https://ik.imagekit.io/28926333pl/tours/vinpearl_nhatrang.jpg', 'AVAILABLE'),
(13, 4, 2, 'Cam Ranh – Biển Bãi Dài Resort 5⭐', 'Resort view biển siêu đẹp.', 'Khánh Hòa, Việt Nam', 7200000.00, 3, 20, 'https://ik.imagekit.io/28926333pl/tours/cam_ranh_baidai.jpg', 'AVAILABLE'),

-- Du lịch nước ngoài
(14, 5, 2, 'Bangkok – Pattaya 4N3Đ', 'Chợ nổi, Safari, lễ hội.', 'Thái Lan', 8500000.00, 4, 50, 'https://ik.imagekit.io/28926333pl/tours/bangkok_pattaya.jpg', 'AVAILABLE'),
(15, 5, 2, 'Singapore – Gardens by the Bay', 'Khám phá quốc đảo xanh.', 'Singapore', 11500000.00, 4, 45, 'https://ik.imagekit.io/28926333pl/tours/singapore_gardens.jpg', 'AVAILABLE'),
(16, 5, 2, 'Hàn Quốc – Seoul 5N4Đ', 'Gyeongbokgung, Namsan Tower, Myeongdong.', 'Seoul, Hàn Quốc', 16500000.00, 5, 30, 'https://ik.imagekit.io/28926333pl/tours/seoul_tour.jpg', 'AVAILABLE'),

-- Sinh thái
(17, 6, 2, 'Rừng Tràm Trà Sư – Sinh thái miền Tây', 'Đi xuồng, rừng ngập nước.', 'An Giang, Việt Nam', 900000.00, 1, 70, 'https://ik.imagekit.io/28926333pl/tours/tra_su_ecotour.jpg', 'AVAILABLE'),
(18, 6, 2, 'Vườn Quốc Gia Cúc Phương', 'Khám phá rừng nguyên sinh.', 'Ninh Bình, Việt Nam', 1800000.00, 2, 40, 'https://ik.imagekit.io/28926333pl/tours/cucphuong_forest.jpg', 'AVAILABLE'),

-- Ẩm thực
(19, 7, 2, 'Tour Ẩm Thực Sài Gòn – Night Food Tour', 'Xe máy + ăn đặc sản địa phương.', 'TP.HCM, Việt Nam', 1200000.00, 1, 80, 'https://ik.imagekit.io/28926333pl/tours/sg_foodtour.jpg', 'AVAILABLE'),
(20, 7, 2, 'Hà Nội Street Food Tour', 'Phở, bún chả, nem, cà phê trứng.', 'Hà Nội, Việt Nam', 1000000.00, 1, 90, 'https://ik.imagekit.io/28926333pl/tours/hanoi_foodtour.jpg', 'AVAILABLE');
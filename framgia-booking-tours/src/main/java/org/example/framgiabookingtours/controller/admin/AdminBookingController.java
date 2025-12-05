package org.example.framgiabookingtours.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.framgiabookingtours.dto.request.AdminDashboardStatsDTO; // Đảm bảo đúng package DTO của bạn
import org.example.framgiabookingtours.entity.Booking;
import org.example.framgiabookingtours.service.BookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
@Slf4j
public class AdminBookingController {

    private final BookingService bookingService;

    /**
     * Hiển thị trang danh sách Booking KÈM Thống kê
     * URL: GET /admin/bookings
     */
    @GetMapping
    public String showBookingsList(Model model) {
        // 1. Lấy danh sách booking
        List<Booking> bookingsList = bookingService.getAllBookings();

        // 2. Lấy số liệu thống kê (Gộp vào đây để hiển thị trên cùng 1 trang)
        // Lưu ý: Kiểm tra lại tên hàm trong Service của bạn là getBookingStats hay getDashboardStats
        AdminDashboardStatsDTO stats = bookingService.getBookingStats();

        // 3. Đưa dữ liệu vào Model
        model.addAttribute("bookingsList", bookingsList);
        model.addAttribute("stats", stats);

        // 4. Trả về file HTML
        return "admin/bookings-list";
    }

}
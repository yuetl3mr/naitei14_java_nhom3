package org.example.framgiabookingtours.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.framgiabookingtours.dto.request.AdminDashboardStatsDTO; // Đảm bảo đúng package DTO của bạn
import org.example.framgiabookingtours.entity.Booking;
import org.example.framgiabookingtours.enums.BookingStatus;
import org.example.framgiabookingtours.service.BookingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {

    private final BookingService bookingService;


    @PostMapping("/{id}/confirm")
    public String confirmBooking(@PathVariable("id") Long bookingId, RedirectAttributes redirectAttributes) {
        try {
            bookingService.adminApproveBooking(bookingId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã duyệt thành công Booking ID: " + bookingId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/bookings";
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable("id") Long bookingId, RedirectAttributes redirectAttributes) {
        try {
            bookingService.adminRejectBooking(bookingId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy đơn và hoàn slot thành công cho Booking ID: " + bookingId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/bookings";
    }

    @GetMapping
    public String showBookingsList(
            Model model,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) BookingStatus status,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        List<Booking> bookingsList = bookingService.searchBookings(keyword, status, fromDate, toDate);

        var stats = bookingService.getBookingStats();

        model.addAttribute("bookingsList", bookingsList);
        model.addAttribute("stats", stats);

        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        return "admin/bookings-list";
    }
}
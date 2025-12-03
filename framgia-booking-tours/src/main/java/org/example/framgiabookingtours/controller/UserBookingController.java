package org.example.framgiabookingtours.controller;

import org.example.framgiabookingtours.dto.request.BookingFormDTO;
import org.example.framgiabookingtours.dto.request.BookingRequestDTO;
import org.example.framgiabookingtours.dto.response.BookingResponseDTO;
import org.example.framgiabookingtours.dto.response.PaymentResponseDTO;
import org.example.framgiabookingtours.exception.AppException;
import org.example.framgiabookingtours.entity.Tour;
import org.example.framgiabookingtours.exception.ErrorCode;
import org.example.framgiabookingtours.service.BookingService;
import org.example.framgiabookingtours.service.PaymentService;
import org.example.framgiabookingtours.service.TourService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserBookingController {

    private final BookingService bookingService;
    private final TourService tourService;
    private final PaymentService paymentService;


    @GetMapping("/my-bookings")
    public String showMyBookings(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();


        List<BookingResponseDTO> bookings = bookingService.getMyBookings(currentUsername);

        model.addAttribute("bookings", bookings);

        return "my-bookings";
    }

    @PostMapping("/bookings/create")
    public String createBooking(@Valid @ModelAttribute("bookingForm") BookingFormDTO bookingForm,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        String userEmail = getCurrentUserEmail();

        if (bindingResult.hasErrors()) {
            log.error("Lỗi Form: {}", bindingResult.getAllErrors());
            Tour tour = tourService.getTourById(bookingForm.getTourId())
                    .orElseThrow(() -> new AppException(ErrorCode.TOUR_NOT_FOUND));
            model.addAttribute("tour", tour);
            return "tour-detail";
        }

        try {
            BookingRequestDTO apiRequest = new BookingRequestDTO();
            apiRequest.setTourId(bookingForm.getTourId());
            apiRequest.setStartDate(bookingForm.getStartDate());
            apiRequest.setNumPeople(bookingForm.getNumPeople());

            bookingService.createBooking(apiRequest, userEmail);

            redirectAttributes.addFlashAttribute("successMessage", "Đặt tour thành công! Vui lòng thanh toán.");

            return "redirect:/my-bookings";

        } catch (Exception e) {
            log.error("Lỗi khi tạo booking: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/tours/" + bookingForm.getTourId();
        }
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User chưa được xác thực");
        }
        return authentication.getName();
    }

    @PostMapping("/bookings/{id}/payment")
    public String payForBooking(@PathVariable("id") Long bookingId,
                                HttpServletRequest request, // <-- Cần cho VNPAY
                                RedirectAttributes redirectAttributes) {
        try {
            String userEmail = getCurrentUserEmail();

            PaymentResponseDTO response = paymentService.createPaymentUrl(bookingId, userEmail, request);

            return "redirect:" + response.getPaymentUrl();

        } catch (Exception e) {
            log.error("Lỗi khi tạo link thanh toán: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/my-bookings";
        }
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable("id") Long bookingId, RedirectAttributes redirectAttributes) {
        try {
            String userEmail = getCurrentUserEmail();

            bookingService.cancelBooking(bookingId, userEmail);

            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy booking thành công.");

        } catch (Exception e) {
            log.error("Lỗi khi hủy booking: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/my-bookings";
    }

}

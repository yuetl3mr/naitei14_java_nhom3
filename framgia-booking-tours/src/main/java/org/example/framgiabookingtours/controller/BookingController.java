package org.example.framgiabookingtours.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.framgiabookingtours.dto.ApiResponse;
import org.example.framgiabookingtours.dto.request.BookingRequestDTO;
import org.example.framgiabookingtours.dto.response.BookingResponseDTO;
import org.example.framgiabookingtours.dto.response.PaymentResponseDTO;
import org.example.framgiabookingtours.entity.Booking;
import org.example.framgiabookingtours.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.framgiabookingtours.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final PaymentService paymentService;


    @GetMapping("/my-bookings")
    public ApiResponse<List<BookingResponseDTO>> getMyBookings() {
        String userEmail = getCurrentUserEmail();
        List<BookingResponseDTO> bookings = bookingService.getMyBookings(userEmail);

        return ApiResponse.<List<BookingResponseDTO>>builder()
                .code(1000)
                .message("Lấy danh sách booking thành công")
                .result(bookings)
                .build();
    }

    @PostMapping
    public ApiResponse<BookingResponseDTO> createBooking(
            @Valid @RequestBody BookingRequestDTO request) {
        String userEmail = getCurrentUserEmail();
        BookingResponseDTO response = bookingService.createBooking(request, userEmail);

        return ApiResponse.<BookingResponseDTO>builder()
                .code(1000)
                .message("Tạo booking thành công")
                .result(response)
                .build();
    }

    @PostMapping("/{bookingId}/payment")
    public ResponseEntity<PaymentResponseDTO> createPayment(
            @PathVariable Long bookingId,
            HttpServletRequest request
    ) {
        String userEmail = getCurrentUserEmail();
        PaymentResponseDTO response = paymentService.createPaymentUrl(bookingId, userEmail, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{bookingId}/cancel")
    public ApiResponse<Booking> cancelBooking(
            @PathVariable Long bookingId
    ) {
        String userEmail = getCurrentUserEmail();
        Booking booking = bookingService.cancelBooking(bookingId, userEmail);

        return ApiResponse.<Booking>builder()
                .code(1000)
                .message("Hủy booking thành công")
                .result(booking)
                .build();
    }
    // chính ra là phải đợi có đăng nhập và authentication mới lấy đc email, nhưng mà lâu quá nên push lên trước sửa sau :)
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User chưa được xác thực");
        }
        return authentication.getName();
    }
}

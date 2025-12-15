package org.example.framgiabookingtours.service;

import org.example.framgiabookingtours.dto.request.AdminDashboardStatsDTO;
import org.example.framgiabookingtours.dto.request.BookingRequestDTO;
import org.example.framgiabookingtours.dto.response.BookingResponseDTO;
import org.example.framgiabookingtours.entity.Booking;
import org.example.framgiabookingtours.enums.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    BookingResponseDTO createBooking(BookingRequestDTO request, String userEmail);
    BookingResponseDTO cancelBooking(Long bookingId, String userEmail);
    List<BookingResponseDTO> getMyBookings(String userEmail);
    AdminDashboardStatsDTO getBookingStats();
    List<Booking> getAllBookings();
    List<Booking> getBookingsByUserId(Long userId);
    Booking adminApproveBooking(Long bookingId);
    Booking adminRejectBooking(Long bookingId);
    List<Booking> searchBookings(String keyword, BookingStatus status, LocalDate fromDate, LocalDate toDate);
}


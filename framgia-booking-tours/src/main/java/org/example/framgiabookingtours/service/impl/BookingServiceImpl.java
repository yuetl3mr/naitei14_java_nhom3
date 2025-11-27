package org.example.framgiabookingtours.service.impl;

import org.example.framgiabookingtours.dto.request.BookingRequestDTO;
import org.example.framgiabookingtours.dto.response.BookingResponseDTO;
import org.example.framgiabookingtours.entity.Booking;
import org.example.framgiabookingtours.entity.Tour;
import org.example.framgiabookingtours.entity.User;
import org.example.framgiabookingtours.enums.BookingStatus;
import org.example.framgiabookingtours.enums.TourStatus;
import org.example.framgiabookingtours.exception.AppException;
import org.example.framgiabookingtours.exception.ErrorCode;
import org.example.framgiabookingtours.repository.BookingRepository;
import org.example.framgiabookingtours.repository.TourRepository;
import org.example.framgiabookingtours.repository.UserRepository;
import org.example.framgiabookingtours.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Tour tour = tourRepository.findById(request.getTourId())
                .orElseThrow(() -> new AppException(ErrorCode.TOUR_NOT_FOUND));

        if (tour.getStatus() != TourStatus.AVAILABLE) {
            throw new AppException(ErrorCode.TOUR_NOT_AVAILABLE);
        }
        if (tour.getAvailableSlots() < request.getNumPeople()) {
            throw new AppException(ErrorCode.TOUR_NOT_ENOUGH_SLOTS, tour.getAvailableSlots());
        }

        tour.setAvailableSlots(tour.getAvailableSlots() - request.getNumPeople());
        tourRepository.save(tour);

        BigDecimal totalPrice = tour.getPrice().multiply(new BigDecimal(request.getNumPeople()));

        Booking booking = Booking.builder()
                .user(user)
                .tour(tour)
                .startDate(request.getStartDate())
                .numPeople(request.getNumPeople())
                .totalPrice(totalPrice)
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        return BookingResponseDTO.fromEntity(savedBooking);
    }

    @Override
    @Transactional
    public Booking cancelBooking(Long bookingId, String userEmail) {

        Booking booking = findBookingByIdAndUserEmail(bookingId, userEmail);


        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new AppException(ErrorCode.TOUR_NOT_ENOUGH_SLOTS, booking.getStatus());
        }

        Tour tour = booking.getTour();
        if (tour != null) {
            tour.setAvailableSlots(tour.getAvailableSlots() + booking.getNumPeople());
            tourRepository.save(tour);
            log.info("User Hủy: Đã hoàn lại {} chỗ cho Tour ID: {}", booking.getNumPeople(), tour.getId());
        }


        if(booking.getStatus() == BookingStatus.PAID) {
            log.warn("Booking ID: {} đã được thanh toán (PAID). User hủy yêu cầu xử lý hoàn tiền (REFUND) thủ công!", bookingId);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        log.info("User đã hủy (Cancelled) Booking ID: {}", bookingId);
        return bookingRepository.save(booking);
    }

    private Booking findBookingByIdAndUserEmail(Long bookingId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.TOUR_NOT_FOUND));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.BOOKING_NOT_BELONG_TO_USER);
        }
        return booking;
    }

    @Override
    public List<BookingResponseDTO> getMyBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Booking> bookings = bookingRepository.findByUserId(user.getId());

        return bookings.stream()
                .map(BookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}


package org.example.framgiabookingtours.service.impl;

import org.example.framgiabookingtours.dto.request.BookingRequestDTO;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Booking createBooking(BookingRequestDTO request, String userEmail) {

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

        return bookingRepository.save(booking);
    }

}


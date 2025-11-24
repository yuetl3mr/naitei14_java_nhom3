package org.example.framgiabookingtours.service;

import org.example.framgiabookingtours.dto.request.BookingRequestDTO;
import org.example.framgiabookingtours.entity.Booking;

public interface BookingService {

    Booking createBooking(BookingRequestDTO request, String userEmail);

}


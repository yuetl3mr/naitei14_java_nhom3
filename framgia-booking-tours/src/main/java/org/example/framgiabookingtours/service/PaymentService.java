package org.example.framgiabookingtours.service;

import org.example.framgiabookingtours.dto.response.PaymentResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    PaymentResponseDTO createPaymentUrl(Long bookingId, String userEmail, HttpServletRequest request);
    String handlePaymentCallback(HttpServletRequest request);
}
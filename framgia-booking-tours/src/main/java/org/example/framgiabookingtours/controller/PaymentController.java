package org.example.framgiabookingtours.controller;

import org.example.framgiabookingtours.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import java.net.URI;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/vnpay-callback")
    public ResponseEntity<?> handlePaymentCallback(HttpServletRequest request) {

        String result = paymentService.handlePaymentCallback(request);

        if (result.startsWith("REDIRECT:")) {
            String redirectUrl = result.substring("REDIRECT:".length());
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(redirectUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            return new ResponseEntity<>(result, headers, HttpStatus.OK);
        }
    }
}

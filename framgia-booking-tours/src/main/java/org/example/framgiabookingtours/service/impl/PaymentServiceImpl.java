package org.example.framgiabookingtours.service.impl;

import org.example.framgiabookingtours.dto.response.PaymentResponseDTO;
import org.example.framgiabookingtours.exception.AppException;
import org.example.framgiabookingtours.exception.ErrorCode;
import org.example.framgiabookingtours.entity.Booking;
import org.example.framgiabookingtours.entity.Payment;
import org.example.framgiabookingtours.entity.User;
import org.example.framgiabookingtours.enums.BookingStatus;
import org.example.framgiabookingtours.enums.PaymentStatus;
import org.example.framgiabookingtours.repository.BookingRepository;
import org.example.framgiabookingtours.repository.PaymentRepository;
import org.example.framgiabookingtours.repository.UserRepository;
import org.example.framgiabookingtours.service.PaymentService;
import org.example.framgiabookingtours.vnpay.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final VNPayService vnPayService;

    @Value("${frontend.payment.successUrl}")
    private String frontendSuccessUrl;

    @Value("${frontend.payment.failUrl}")
    private String frontendFailUrl;

    @Override
    @Transactional
    public PaymentResponseDTO createPaymentUrl(Long bookingId, String userEmail, HttpServletRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.TOUR_NOT_FOUND));

        // 1. Kiểm tra quyền sở hữu
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.BOOKING_NOT_BELONG_TO_USER);
        }

        // 2. Kiểm tra trạng thái booking
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new AppException(ErrorCode.BOOKING_COMPLETE);
        }

        // 3. Tạo một record Payment mới
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalPrice())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // 4. LẤY LOGIC TỪ VNPAY-DEMO CONTROLLER
        //
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        int total = booking.getTotalPrice().intValue();

        // vnp_OrderInfo nên là một mã duy nhất, chúng ta dùng paymentId
        String orderInfo = String.valueOf(savedPayment.getId());

        String vnpayUrl = vnPayService.createOrder(total, orderInfo, baseUrl);

        // 5. Lưu lại URL thanh toán vào CSDL
//        savedPayment.setPaymentUrl(vnpayUrl);
        paymentRepository.save(savedPayment);

        return PaymentResponseDTO.builder()
                .status("OK")
                .message("Tạo link thanh toán thành công")
                .paymentUrl(vnpayUrl) // Đây là URL thật của VNPAY
                .build();
    }
    @Override
    @Transactional
    public String handlePaymentCallback(HttpServletRequest request) {
        String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");
        String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");

        log.info("--- VNPAY CALLBACK ---");
        log.info("OrderInfo (PaymentId): {}", vnp_OrderInfo);
        log.info("TransactionNo (VNPAY): {}", vnp_TransactionNo);
        log.info("ResponseCode (VNPAY): {}", vnp_ResponseCode);

        // 1. Kiểm tra cấu hình (bạn đã thêm ở bước trước)
        if (frontendSuccessUrl == null || frontendSuccessUrl.isEmpty()) {
            log.error("LỖI CẤU HÌNH: frontend.payment.successUrl chưa được cài đặt!");
            return "<h1>LỖI SERVER NGHIÊM TRỌNG</h1><p><b>Lỗi:</b> 'frontend.payment.successUrl' chưa được cấu hình.</p>";
        }

        // 2. Kiểm tra paymentId
        if (vnp_OrderInfo == null || vnp_OrderInfo.isEmpty()) {
            log.warn("LỖI: vnp_OrderInfo (paymentId) bị rỗng.");
            return "<h1>Lỗi Thanh Toán</h1><p>Lỗi: Không tìm thấy 'vnp_OrderInfo' (paymentId).</p>";
        }

        // 3. Xác thực chữ ký
        int paymentStatus = vnPayService.orderReturn(request);
        log.info("Payment Status (1=OK, 0=FAIL, -1=INVALID_SIG): {}", paymentStatus);

        // 4. Tìm Payment
        Long paymentId = Long.parseLong(vnp_OrderInfo);
        Payment payment = paymentRepository.findById(paymentId).orElse(null);

        if (payment == null) {
            log.error("LỖI CSDL: Không tìm thấy Payment với ID: {}", paymentId);
            return String.format("<h1>Lỗi Thanh Toán</h1><p>Lỗi: Không tìm thấy giao dịch (Payment) với ID: %s.</p>", vnp_OrderInfo);
        }

        // 5. Kiểm tra trạng thái PENDING
        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            log.warn("CẢNH BÁO: Giao dịch này đã được xử lý (Status: {}). Bỏ qua.", payment.getPaymentStatus());
            if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
                return "REDIRECT:" + frontendSuccessUrl + "?bookingId=" + payment.getBooking().getId();
            }
            return "<h1>Lỗi Thanh Toán</h1><p>Giao dịch này đã được xử lý.</p>";
        }

        // 6. Xử lý kết quả
        if (paymentStatus == 1) {
            log.info(">>> THANH TOÁN THÀNH CÔNG (SUCCESS) <<<");
            Booking booking = payment.getBooking();

            if (booking == null) {
                log.error("LỖI CSDL NGHIÊM TRỌNG: Payment ID {} không liên kết với Booking nào (booking is null). Rollback!", paymentId);
                return "<h1>Lỗi CSDL</h1><p>Lỗi: Không tìm thấy Booking liên kết với Payment ID: " + paymentId + ".</p>";
            }

            payment.setPaymentStatus(PaymentStatus.SUCCESS);
//            payment.setTransactionId(vnp_TransactionNo);
//            payment.setVnpTxnRef(request.getParameter("vnp_TxnRef"));
            booking.setStatus(BookingStatus.PAID);

            paymentRepository.save(payment);
            bookingRepository.save(booking);
            log.info("Đã cập nhật Payment ID: {} và Booking ID: {} sang PAID.", paymentId, booking.getId());

            return "REDIRECT:" + frontendSuccessUrl + "?bookingId=" + booking.getId();

        } else if (paymentStatus == 0) {
            log.warn(">>> THANH TOÁN THẤT BẠI (FAIL) <<<");
            payment.setPaymentStatus(PaymentStatus.FAILED);
//            payment.setTransactionId(vnp_TransactionNo);
            paymentRepository.save(payment);

            return String.format("<h1>Thanh Toán Thất Bại</h1><p>VNPAY báo lỗi ResponseCode: %s.</p>", vnp_ResponseCode);

        } else {
            log.error(">>> THANH TOÁN LỖI: SAI CHỮ KÝ (INVALID SIGNATURE) <<<");
            log.error("Kiểm tra lại 'vnp_HashSecret' trong application.properties!");
            return "<h1>LỖI NGHIÊM TRỌNG: Sai Chữ Ký (Invalid Signature)</h1>"
                    + "<p>Lỗi: vnp_SecureHash không khớp.</p>"
                    + "<p><b>Nguyên nhân:</b> 'vnp_HashSecret' trong file 'application.properties' của bạn bị <b>SAI</b>.</p>";
        }
    }


}


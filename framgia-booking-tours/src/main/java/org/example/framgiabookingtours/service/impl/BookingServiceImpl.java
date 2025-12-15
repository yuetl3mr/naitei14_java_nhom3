package org.example.framgiabookingtours.service.impl;

import org.example.framgiabookingtours.dto.request.AdminDashboardStatsDTO;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

        Tour tour = tourRepository.findByIdWithLock(request.getTourId())
                .orElseThrow(() -> new AppException(ErrorCode.TOUR_NOT_FOUND));

        if (tour.getStatus() != TourStatus.AVAILABLE) {
            throw new AppException(ErrorCode.TOUR_NOT_AVAILABLE);
        }

        if (tour.getAvailableSlots() < request.getNumPeople()) {
            throw new AppException(ErrorCode.TOUR_NOT_ENOUGH_SLOTS, tour.getAvailableSlots());
        }

        tour.setAvailableSlots(tour.getAvailableSlots() - request.getNumPeople());
        tourRepository.save(tour);

        BigDecimal totalPrice = tour.getPrice().multiply(BigDecimal.valueOf(request.getNumPeople()));

        Booking booking = Booking.builder()
                .user(user)
                .tour(tour)
                .startDate(request.getStartDate())
                .bookingDate(LocalDateTime.now())
                .numPeople(request.getNumPeople())
                .totalPrice(totalPrice)
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        return BookingResponseDTO.fromEntity(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(Long bookingId, String userEmail) {
        Booking booking = findBookingByIdAndUserEmail(bookingId, userEmail);

        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.PAID) {
            throw new AppException(ErrorCode.BOOKING_CANNOT_CANCEL, booking.getStatus());
        }

        Tour tour = tourRepository.findByIdWithLock(booking.getTour().getId())
                .orElseThrow(() -> new AppException(ErrorCode.TOUR_NOT_FOUND));

        if (tour != null) {
            tour.setAvailableSlots(tour.getAvailableSlots() + booking.getNumPeople());
            tourRepository.save(tour);
            log.info("User Hủy: Đã hoàn lại {} chỗ cho Tour ID: {}", booking.getNumPeople(), tour.getId());
        }

        if(booking.getStatus() == BookingStatus.PAID) {
            log.warn("Booking ID: {} đã được thanh toán (PAID). User hủy yêu cầu xử lý hoàn tiền (REFUND) thủ công!", bookingId);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking savedBooking = bookingRepository.save(booking);

        log.info("User {} đã hủy thành công Booking ID: {}", userEmail, bookingId);

        return BookingResponseDTO.fromEntity(savedBooking);
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

    @Override
    public AdminDashboardStatsDTO getBookingStats() {
        log.info("Admin đang lấy thông tin thống kê Dashboard...");

        long pending = bookingRepository.countByStatus(BookingStatus.PENDING);
        long paid = bookingRepository.countByStatus(BookingStatus.PAID);
        long confirmed = bookingRepository.countByStatus(BookingStatus.CONFIRMED);
        long cancelled = bookingRepository.countByStatus(BookingStatus.CANCELLED);
        long total = pending + paid + confirmed + cancelled;

        // (Bạn cũng có thể dùng bookingRepository.count() để lấy total)

        return AdminDashboardStatsDTO.builder()
                .totalPending(pending)
                .totalPaid(paid)
                .totalConfirmed(confirmed)
                .totalCancelled(cancelled)
                .totalBookings(total)
                .build();
    }

    @Override
    public List<Booking> getAllBookings() {
        log.info("Admin đang lấy tất cả booking...");
        return bookingRepository.findAllWithUserAndTour();
    }
    
    @Override
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserIdOrderByBookingDateDesc(userId);
    }

    @Override
    @Transactional
    public Booking adminApproveBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() != BookingStatus.PAID) {
            throw new AppException(ErrorCode.BOOKING_CANNOT_CONFIRM);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        log.info("Admin đã DUYỆT (Confirmed) Booking ID: {}", bookingId);

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking adminRejectBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new AppException(ErrorCode.BOOKING_ALREADY_CANCELLED);
        }

        Tour tour = booking.getTour();
        if (tour != null) {
            int currentSlots = tour.getAvailableSlots();
            int returnSlots = booking.getNumPeople();

            tour.setAvailableSlots(currentSlots + returnSlots);
            tourRepository.save(tour);

            log.info("Admin Hủy: Đã hoàn lại {} chỗ cho Tour ID: {}. Slot mới: {}",
                    returnSlots, tour.getId(), tour.getAvailableSlots());
        }

        if(booking.getStatus() == BookingStatus.PAID || booking.getStatus() == BookingStatus.CONFIRMED) {
            log.warn("CẢNH BÁO: Booking ID {} trạng thái cũ là {}. Admin cần kiểm tra và HOÀN TIỀN thủ công cho khách!",
                    bookingId, booking.getStatus());
        }

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CANCELLED);

        log.info("Admin đã chuyển trạng thái Booking ID: {} từ {} sang CANCELLED", bookingId, oldStatus);

        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> searchBookings(String keyword, BookingStatus status, LocalDate fromDate, LocalDate toDate) {
        LocalDateTime startDateTime = (fromDate != null) ? fromDate.atStartOfDay() : null; // 00:00:00
        LocalDateTime endDateTime = (toDate != null) ? toDate.atTime(LocalTime.MAX) : null; // 23:59:59

        return bookingRepository.searchBookings(keyword, status, startDateTime, endDateTime);
    }
}


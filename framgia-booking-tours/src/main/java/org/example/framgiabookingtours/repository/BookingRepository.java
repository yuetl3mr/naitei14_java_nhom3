package org.example.framgiabookingtours.repository;

import org.example.framgiabookingtours.dto.response.BookingStatusDTO;
import org.example.framgiabookingtours.dto.response.MonthlyRevenueDTO;
import org.example.framgiabookingtours.entity.Booking;
import org.example.framgiabookingtours.enums.BookingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

	@EntityGraph(attributePaths = { "user", "tour" })
	List<Booking> findByUserId(Long userId);

	@EntityGraph(attributePaths = { "user", "tour", "user.profile" })
	@Query("SELECT b FROM Booking b ORDER BY b.bookingDate DESC") // Sắp xếp mới nhất lên đầu
	List<Booking> findAllWithUserAndTour();

	long countByStatus(BookingStatus status);

	// Tính tổng doanh thu của các booking đã thanh toán (PAID)
	@Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.status = 'PAID'")
	BigDecimal sumTotalRevenue();

	// Đếm số booking trong khoảng thời gian (Dùng cho Booking hôm nay)
	long countByBookingDateBetween(LocalDateTime start, LocalDateTime end);

	// Tính doanh thu theo tháng cụ thể để tính tăng trưởng
	@Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.status = 'PAID' AND MONTH(b.bookingDate) = :month AND YEAR(b.bookingDate) = :year")
	BigDecimal sumRevenueByMonth(@Param("month") int month, @Param("year") int year);

	List<Booking> findByUserIdOrderByBookingDateDesc(Long userId);

	// Thống kê Doanh thu theo tháng trong một năm cụ thể
	// Chỉ tính các booking đã PAID
	@Query("SELECT new org.example.framgiabookingtours.dto.response.MonthlyRevenueDTO(MONTH(b.bookingDate), SUM(b.totalPrice)) "
			+ "FROM Booking b " + "WHERE b.status = 'PAID' AND YEAR(b.bookingDate) = :year "
			+ "GROUP BY MONTH(b.bookingDate) " + "ORDER BY MONTH(b.bookingDate)")
	List<MonthlyRevenueDTO> getMonthlyRevenue(@Param("year") int year);

	// 2. Thống kê số lượng theo Trạng thái
	@Query("SELECT new org.example.framgiabookingtours.dto.response.BookingStatusDTO(b.status, COUNT(b)) "
			+ "FROM Booking b " + "GROUP BY b.status")
	List<BookingStatusDTO> getBookingStatusStats();
}

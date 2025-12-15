package org.example.framgiabookingtours.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.framgiabookingtours.dto.response.AdminDashboardStatsDTO;
import org.example.framgiabookingtours.dto.response.BookingStatusDTO;
import org.example.framgiabookingtours.dto.response.MonthlyRevenueDTO;
import org.example.framgiabookingtours.enums.TourStatus;
import org.example.framgiabookingtours.repository.BookingRepository;
import org.example.framgiabookingtours.repository.TourRepository;
import org.example.framgiabookingtours.repository.UserRepository;
import org.example.framgiabookingtours.service.DashboardService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

	private final BookingRepository bookingRepository;
	private final UserRepository userRepository;
	private final TourRepository tourRepository;

	@Override
	public AdminDashboardStatsDTO getDashboardStats() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
		LocalDateTime startOf7DaysAgo = now.minusDays(7);

		// 1. Tính Doanh thu
		BigDecimal totalRevenue = bookingRepository.sumTotalRevenue();
		// (Logic tính % tăng trưởng tạm thời để 0 hoặc random cho đẹp giao diện trước,
		// làm kỹ sau)
		double revenueGrowth = 12.5;

		// 2. Booking hôm nay
		long todayBookings = bookingRepository.countByBookingDateBetween(startOfToday, now);

		// 3. User mới 7 ngày
		long newUsers = userRepository.countByCreatedAtAfter(startOf7DaysAgo);

		// 4. Tour
		long totalTours = tourRepository.count();
		long activeTours = tourRepository.countByStatus(TourStatus.AVAILABLE);

		return AdminDashboardStatsDTO.builder().totalRevenue(totalRevenue).revenueGrowth(revenueGrowth)
				.todayBookings(todayBookings).newUsers7Days(newUsers).userGrowth(5.2) // Mock tạm
				.totalTours(totalTours).activeTours(activeTours).build();
	}

	@Override
	public List<MonthlyRevenueDTO> getRevenueChartData(int year) {
		// 1. Lấy dữ liệu thô từ DB (chỉ chứa các tháng có doanh thu)
		List<MonthlyRevenueDTO> rawData = bookingRepository.getMonthlyRevenue(year);

		// 2. Tạo Map để tra cứu nhanh: Map<Tháng, Doanh thu>
		Map<Integer, BigDecimal> revenueMap = rawData.stream()
				.collect(Collectors.toMap(MonthlyRevenueDTO::getMonth, MonthlyRevenueDTO::getRevenue));

		// 3. Tạo danh sách đủ 12 tháng (Tháng nào thiếu thì set bằng 0)
		List<MonthlyRevenueDTO> fullYearData = new ArrayList<>();
		for (int i = 1; i <= 12; i++) {
			BigDecimal revenue = revenueMap.getOrDefault(i, BigDecimal.ZERO);
			fullYearData.add(new MonthlyRevenueDTO(i, revenue));
		}

		return fullYearData;
	}

	@Override
	public List<BookingStatusDTO> getStatusChartData() {
		return bookingRepository.getBookingStatusStats();
	}
}
package org.example.framgiabookingtours.service;

import java.util.List;

import org.example.framgiabookingtours.dto.response.AdminDashboardStatsDTO;
import org.example.framgiabookingtours.dto.response.BookingStatusDTO;
import org.example.framgiabookingtours.dto.response.MonthlyRevenueDTO;

public interface DashboardService {
	AdminDashboardStatsDTO getDashboardStats();

	List<MonthlyRevenueDTO> getRevenueChartData(int year);

	List<BookingStatusDTO> getStatusChartData();
}

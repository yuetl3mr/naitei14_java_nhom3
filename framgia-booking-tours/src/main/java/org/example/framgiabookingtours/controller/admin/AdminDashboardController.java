package org.example.framgiabookingtours.controller.admin;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.example.framgiabookingtours.dto.response.AdminDashboardStatsDTO;
import org.example.framgiabookingtours.service.DashboardService;
import org.example.framgiabookingtours.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

	private final DashboardService dashboardService;

	@GetMapping
	public String dashboard(Model model) {
		var user = SecurityUtils.getCurrentUser().orElse(null);
		if (user != null) {
			model.addAttribute("currentUser", user);

			System.out.println("Current user: " + user.getEmail());
			if (user.getProfile() != null) {
				System.out.println("Full name: " + user.getProfile().getFullName());
			}
		}

		model.addAttribute("activeMenu", "dashboard");

		// HARDCODE NĂM 2025 ĐỂ TEST DỮ LIỆU GIẢ
		int testYear = 2025;

		// Lấy dữ liệu
		var revenueData = dashboardService.getRevenueChartData(testYear);
		var statusData = dashboardService.getStatusChartData();
		var kpiData = dashboardService.getDashboardStats();

		// Chuyển Object thành JSON String để hiển thị lên HTML
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonCharts = mapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(Map.of("revenueChart", revenueData, "statusChart", statusData));

			String jsonKPI = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(kpiData);

			// Gửi sang View
			model.addAttribute("debugCharts", jsonCharts);
			model.addAttribute("debugKPI", jsonKPI);

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return "admin/dashboard";
	}

	// --- TEST JSON ---
	@GetMapping("/test-api")
	@ResponseBody
	public ResponseEntity<AdminDashboardStatsDTO> getDashboardStatsAPI() {
		AdminDashboardStatsDTO stats = dashboardService.getDashboardStats();
		return ResponseEntity.ok(stats);
	}

	@GetMapping("/test-charts")
	@ResponseBody
	public ResponseEntity<?> getChartDataAPI() {
		int currentYear = 2025; // Hardcode 2025 để test
		return ResponseEntity.ok(Map.of("revenueChart", dashboardService.getRevenueChartData(currentYear),
				"statusChart", dashboardService.getStatusChartData()));
	}
}
package org.example.framgiabookingtours.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder // Dùng @Builder để tạo đối tượng dễ dàng
public class AdminDashboardStatsDTO {
    private long totalPending;
    private long totalPaid;
    private long totalConfirmed;
    private long totalCancelled;
    private long totalBookings;
}

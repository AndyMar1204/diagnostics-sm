package com.andy.gstockapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private BigDecimal totalRevenue;
    private Long totalInvoicesCount;
    private Long totalClientsCount;
    private List<ChartDataDTO> salesHistory;
    private List<ChartDataDTO> topProducts;
    private List<ProductResponse> lowStockProducts;
}

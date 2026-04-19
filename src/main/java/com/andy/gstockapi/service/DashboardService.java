package com.andy.gstockapi.service;

import com.andy.gstockapi.dto.ChartDataDTO;
import com.andy.gstockapi.dto.DashboardStatsResponse;
import com.andy.gstockapi.dto.ProductResponse;
import com.andy.gstockapi.entity.Invoice;
import com.andy.gstockapi.entity.InvoiceItem;
import com.andy.gstockapi.entity.InvoiceType;
import com.andy.gstockapi.mapper.ProductMapper;
import com.andy.gstockapi.repository.ClientRepository;
import com.andy.gstockapi.repository.InvoiceRepository;
import com.andy.gstockapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final ProductMapper productMapper;

    public DashboardStatsResponse getStats() {
        // 1. Basic Counts
        BigDecimal revenue = invoiceRepository.sumTotalAmountByType(InvoiceType.SALE);
        if (revenue == null) revenue = BigDecimal.ZERO;
        
        Long invoiceCount = invoiceRepository.count();
        Long clientCount = invoiceRepository.countDistinctClients();

        // 2. Sales History (Grouped by Month)
        List<Invoice> allSales = invoiceRepository.findAll().stream()
                .filter(i -> i.getType() == InvoiceType.SALE)
                .collect(Collectors.toList());

        Map<String, BigDecimal> salesByMonth = allSales.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getCreatedAt().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        LinkedHashMap::new,
                        Collectors.mapping(Invoice::getTotalAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        List<ChartDataDTO> salesHistory = salesByMonth.entrySet().stream()
                .map(e -> new ChartDataDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        // 3. Top Products (by quantity)
        Map<String, Integer> productSales = new HashMap<>();
        for (Invoice sale : allSales) {
            for (InvoiceItem item : sale.getItems()) {
                String name = item.getProduct().getName();
                productSales.put(name, productSales.getOrDefault(name, 0) + item.getQuantity());
            }
        }

        List<ChartDataDTO> topProducts = productSales.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(e -> new ChartDataDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        // 4. Low Stock Products
        List<ProductResponse> lowStock = productRepository.findByQuantityLessThan(5).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        return DashboardStatsResponse.builder()
                .totalRevenue(revenue)
                .totalInvoicesCount(invoiceCount)
                .totalClientsCount(clientCount)
                .salesHistory(salesHistory)
                .topProducts(topProducts)
                .lowStockProducts(lowStock)
                .build();
    }
}

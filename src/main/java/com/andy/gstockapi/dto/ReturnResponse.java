package com.andy.gstockapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnResponse {
    private Integer id;
    private String reference;
    private String reason;
    private LocalDateTime returnDate;
    private BigDecimal totalAmount;
    private String invoiceReference;
    private List<ReturnItemResponse> items;
}

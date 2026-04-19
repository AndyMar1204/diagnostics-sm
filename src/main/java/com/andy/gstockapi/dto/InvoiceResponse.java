package com.andy.gstockapi.dto;

import com.andy.gstockapi.entity.InvoiceStatus;
import com.andy.gstockapi.entity.InvoiceType;
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
public class InvoiceResponse {
    private Integer id;
    private String name;
    private String reference;
    private InvoiceType type;
    private InvoiceStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private ClientDTO client;
    private List<InvoiceItemResponse> items;
}

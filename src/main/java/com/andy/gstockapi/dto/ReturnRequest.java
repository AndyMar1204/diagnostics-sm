package com.andy.gstockapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequest {
    @NotNull(message = "Invoice ID is required")
    private Integer invoiceId;
    
    private String reason;
    
    @NotEmpty(message = "Return items cannot be empty")
    @Valid
    private List<ReturnItemRequest> items;
}

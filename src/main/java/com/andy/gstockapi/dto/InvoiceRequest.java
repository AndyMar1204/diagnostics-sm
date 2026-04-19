package com.andy.gstockapi.dto;

import com.andy.gstockapi.entity.InvoiceType;
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
public class InvoiceRequest {
    @NotNull(message = "Invoice type is required")
    private InvoiceType type;
    
    @NotNull(message = "Client is required")
    private ClientDTO client;
    
    @NotEmpty(message = "Invoice items cannot be empty")
    @Valid
    private List<InvoiceItemRequest> items;
}

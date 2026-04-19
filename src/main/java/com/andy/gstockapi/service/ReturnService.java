package com.andy.gstockapi.service;

import com.andy.gstockapi.dto.ReturnItemRequest;
import com.andy.gstockapi.dto.ReturnRequest;
import com.andy.gstockapi.dto.ReturnResponse;
import com.andy.gstockapi.entity.*;
import com.andy.gstockapi.exception.ResourceNotFoundException;
import com.andy.gstockapi.mapper.ProductMapper;
import com.andy.gstockapi.repository.InvoiceRepository;
import com.andy.gstockapi.repository.ProductRepository;
import com.andy.gstockapi.repository.ReturnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ReturnResponse createReturn(ReturnRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found id: " + request.getInvoiceId()));

        CustomerReturn customerReturn = CustomerReturn.builder()
                .reference("RET-" + invoice.getReference())
                .invoice(invoice)
                .returnDate(LocalDateTime.now())
                .reason(request.getReason())
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (ReturnItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found id: " + itemReq.getProductId()));

            // Restore stock
            product.setQuantity(product.getQuantity() + itemReq.getQuantity());
            productRepository.save(product);

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            ReturnItem returnItem = ReturnItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getPrice())
                    .totalPrice(itemTotal)
                    .customerReturn(customerReturn)
                    .build();

            customerReturn.getItems().add(returnItem);
        }

        customerReturn.setTotalAmount(totalAmount);
        
        // Update invoice status if fully returned? For now just mark as RETURNED if it's a significant return
        invoice.setStatus(InvoiceStatus.RETURNED);
        invoiceRepository.save(invoice);

        CustomerReturn saved = returnRepository.save(customerReturn);
        return mapToResponse(saved);
    }

    public List<ReturnResponse> getAllReturns() {
        return returnRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ReturnResponse mapToResponse(CustomerReturn ret) {
        return ReturnResponse.builder()
                .id(ret.getId())
                .reference(ret.getReference())
                .reason(ret.getReason())
                .returnDate(ret.getReturnDate())
                .totalAmount(ret.getTotalAmount())
                .invoiceReference(ret.getInvoice().getReference())
                .items(ret.getItems().stream().map(item -> 
                    com.andy.gstockapi.dto.ReturnItemResponse.builder()
                        .id(item.getId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .product(productMapper.toDto(item.getProduct()))
                        .build()
                ).collect(Collectors.toList()))
                .build();
    }
}

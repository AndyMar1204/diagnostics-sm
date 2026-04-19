package com.andy.gstockapi.service;

import com.andy.gstockapi.dto.InvoiceItemRequest;
import com.andy.gstockapi.dto.InvoiceRequest;
import com.andy.gstockapi.dto.InvoiceResponse;
import com.andy.gstockapi.entity.*;
import com.andy.gstockapi.exception.InsufficientStockException;
import com.andy.gstockapi.exception.ResourceNotFoundException;
import com.andy.gstockapi.mapper.ClientMapper;
import com.andy.gstockapi.mapper.InvoiceMapper;
import com.andy.gstockapi.repository.ClientRepository;
import com.andy.gstockapi.repository.InvoiceRepository;
import com.andy.gstockapi.repository.ProductRepository;
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
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final InvoiceMapper invoiceMapper;
    private final ClientMapper clientMapper;

    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) {
        // 1. Handle Client
        Client client = clientRepository.save(clientMapper.toEntity(request.getClient()));

        // 2. Prepare Invoice
        Invoice invoice = Invoice.builder()
                .reference(generateReference())
                .type(request.getType())
                .status(InvoiceStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .client(client)
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 3. Process Items
        for (InvoiceItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemReq.getProductId()));

            // Stock check for SALE type
            if (request.getType() == InvoiceType.SALE) {
                if (product.getQuantity() < itemReq.getQuantity()) {
                    throw new InsufficientStockException("Insufficient stock for product: " + product.getName() + 
                        " (Available: " + product.getQuantity() + ")");
                }
                // Deduct stock
                product.setQuantity(product.getQuantity() - itemReq.getQuantity());
                productRepository.save(product);
            }

            BigDecimal itemTotalPrice = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(itemTotalPrice);

            InvoiceItem item = InvoiceItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getPrice())
                    .totalPrice(itemTotalPrice)
                    .invoice(invoice)
                    .build();
            
            invoice.getItems().add(item);
        }

        invoice.setTotalAmount(totalAmount);
        
        return invoiceMapper.toDto(invoiceRepository.save(invoice));
    }

    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(invoiceMapper::toDto)
                .collect(Collectors.toList());
    }

    public InvoiceResponse getInvoiceById(Integer id) {
        return invoiceRepository.findById(id)
                .map(invoiceMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    private String generateReference() {
        long count = invoiceRepository.count() + 1;
        return String.format("FACT-%04d-%05d", LocalDateTime.now().getYear(), count);
    }
}

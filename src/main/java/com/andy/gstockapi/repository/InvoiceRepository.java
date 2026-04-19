package com.andy.gstockapi.repository;

import com.andy.gstockapi.entity.Invoice;
import com.andy.gstockapi.entity.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.type = :type")
    BigDecimal sumTotalAmountByType(InvoiceType type);

    @Query("SELECT COUNT(DISTINCT i.client) FROM Invoice i")
    Long countDistinctClients();
}

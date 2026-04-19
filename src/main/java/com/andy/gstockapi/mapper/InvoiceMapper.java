package com.andy.gstockapi.mapper;

import com.andy.gstockapi.dto.InvoiceItemResponse;
import com.andy.gstockapi.dto.InvoiceResponse;
import com.andy.gstockapi.entity.Invoice;
import com.andy.gstockapi.entity.InvoiceItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ClientMapper.class, ProductMapper.class})
public interface InvoiceMapper {
    @Mapping(target = "name", ignore = true) // reference is used as name in some contexts
    InvoiceResponse toDto(Invoice invoice);

    InvoiceItemResponse toItemDto(InvoiceItem item);
}

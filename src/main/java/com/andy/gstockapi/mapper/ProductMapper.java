package com.andy.gstockapi.mapper;

import com.andy.gstockapi.dto.ProductRequest;
import com.andy.gstockapi.dto.ProductResponse;
import com.andy.gstockapi.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {
    ProductResponse toDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductRequest productRequest);
}

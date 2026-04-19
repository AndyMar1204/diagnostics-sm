package com.andy.gstockapi.mapper;

import com.andy.gstockapi.dto.CategoryDTO;
import com.andy.gstockapi.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDto(Category category);
    Category toEntity(CategoryDTO categoryDTO);
}

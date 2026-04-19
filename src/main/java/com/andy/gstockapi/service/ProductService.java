package com.andy.gstockapi.service;

import com.andy.gstockapi.dto.CategoryDTO;
import com.andy.gstockapi.dto.ProductRequest;
import com.andy.gstockapi.dto.ProductResponse;
import com.andy.gstockapi.entity.Category;
import com.andy.gstockapi.entity.Product;
import com.andy.gstockapi.exception.ResourceNotFoundException;
import com.andy.gstockapi.mapper.CategoryMapper;
import com.andy.gstockapi.mapper.ProductMapper;
import com.andy.gstockapi.repository.CategoryRepository;
import com.andy.gstockapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        
        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        
        return productMapper.toDto(productRepository.save(product));
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO request) {
        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }
}

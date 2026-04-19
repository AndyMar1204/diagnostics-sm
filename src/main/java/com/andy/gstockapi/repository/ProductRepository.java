package com.andy.gstockapi.repository;

import com.andy.gstockapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByQuantityLessThan(Integer threshold);
}

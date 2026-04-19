package com.andy.gstockapi.repository;

import com.andy.gstockapi.entity.CustomerReturn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnRepository extends JpaRepository<CustomerReturn, Integer> {
}

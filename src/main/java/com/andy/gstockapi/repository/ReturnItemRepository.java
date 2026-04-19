package com.andy.gstockapi.repository;

import com.andy.gstockapi.entity.ReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnItemRepository extends JpaRepository<ReturnItem, Integer> {
}

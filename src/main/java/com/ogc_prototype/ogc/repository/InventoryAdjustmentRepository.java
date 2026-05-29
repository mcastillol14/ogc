package com.ogc_prototype.ogc.repository;

import com.ogc_prototype.ogc.model.InventoryAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryAdjustmentRepository extends JpaRepository<InventoryAdjustment, Integer> {

    List<InventoryAdjustment> findAllByOrderByCreatedAtDesc();
}

package com.ogc_prototype.ogc.repository;

import com.ogc_prototype.ogc.model.PurchaseLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseLineRepository extends JpaRepository<PurchaseLine, Integer> {

    List<PurchaseLine> findAllByPurchaseId(Integer purchaseId);

    List<PurchaseLine> findAllByProductId(Integer productId);

    Optional<PurchaseLine> findByIdAndPurchaseId(Integer id, Integer purchaseId);
}

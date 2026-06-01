package com.ogc_prototype.ogc.repository;

import com.ogc_prototype.ogc.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {

    List<Purchase> findAllByProviderId(Integer providerId);

    List<Purchase> findAllByOrderByCreatedAtDesc();
}

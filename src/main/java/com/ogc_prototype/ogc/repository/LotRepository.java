package com.ogc_prototype.ogc.repository;

import com.ogc_prototype.ogc.model.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotRepository extends JpaRepository<Lot, Integer> {

    List<Lot> findAllByProductId(Integer productId);

    List<Lot> findAllByCategoryId(Integer categoryId);

    List<Lot> findAllByProviderId(Integer providerId);
}

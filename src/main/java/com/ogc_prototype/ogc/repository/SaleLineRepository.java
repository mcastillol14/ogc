package com.ogc_prototype.ogc.repository;

import com.ogc_prototype.ogc.model.SaleLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaleLineRepository extends JpaRepository<SaleLine, Integer> {

    List<SaleLine> findAllBySaleId(Integer saleId);

    Optional<SaleLine> findByIdAndSaleId(Integer id, Integer saleId);
}

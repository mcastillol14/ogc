package com.ogc_prototype.ogc.repository;

import com.ogc_prototype.ogc.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Integer> {

    List<Sale> findAllByCustomerId(Integer customerId);
}

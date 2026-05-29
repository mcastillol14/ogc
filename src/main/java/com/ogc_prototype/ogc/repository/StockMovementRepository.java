package com.ogc_prototype.ogc.repository;

import com.ogc_prototype.ogc.model.StockMovement;
import com.ogc_prototype.ogc.model.enums.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Integer> {

    List<StockMovement> findAllByLotId(Integer lotId);

    List<StockMovement> findAllByPurchaseId(Integer purchaseId);

    List<StockMovement> findAllBySaleId(Integer saleId);

    List<StockMovement> findAllByType(MovementType type);
}

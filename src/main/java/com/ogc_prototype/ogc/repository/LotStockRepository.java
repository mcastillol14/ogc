package com.ogc_prototype.ogc.repository;

import com.ogc_prototype.ogc.model.LotStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LotStockRepository extends JpaRepository<LotStock, Integer> {

    Optional<LotStock> findByLotId(Integer lotId);

    @Query("SELECT ls FROM LotStock ls WHERE ls.lot.product.id = :productId AND ls.remainingWeight > 0")
    List<LotStock> findAvailableStockByProductId(@Param("productId") Integer productId);

    @Query("SELECT COALESCE(SUM(ls.remainingWeight), 0) FROM LotStock ls WHERE ls.lot.product.id = :productId")
    Double sumRemainingWeightByProductId(@Param("productId") Integer productId);
}

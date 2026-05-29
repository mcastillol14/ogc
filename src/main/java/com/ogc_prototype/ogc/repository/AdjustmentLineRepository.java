package com.ogc_prototype.ogc.repository;

import com.ogc_prototype.ogc.model.AdjustmentLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdjustmentLineRepository extends JpaRepository<AdjustmentLine, Integer> {

    List<AdjustmentLine> findAllByAdjustmentId(Integer adjustmentId);

    List<AdjustmentLine> findAllByLotId(Integer lotId);
}

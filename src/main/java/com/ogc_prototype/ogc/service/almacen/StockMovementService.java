package com.ogc_prototype.ogc.service.almacen;

import com.ogc_prototype.ogc.dto.response.StockMovementResponse;
import com.ogc_prototype.ogc.model.enums.MovementType;

import java.util.List;

public interface StockMovementService {
    List<StockMovementResponse> getAll();

    List<StockMovementResponse> getByLotId(Integer lotId);

    List<StockMovementResponse> getByPurchaseId(Integer purchaseId);

    List<StockMovementResponse> getBySaleId(Integer saleId);

    List<StockMovementResponse> getByType(MovementType type);
}

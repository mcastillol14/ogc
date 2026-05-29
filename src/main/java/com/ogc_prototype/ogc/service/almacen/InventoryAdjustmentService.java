package com.ogc_prototype.ogc.service.almacen;

import com.ogc_prototype.ogc.dto.request.AdjustmentLineRequest;
import com.ogc_prototype.ogc.dto.request.InventoryAdjustmentRequest;
import com.ogc_prototype.ogc.dto.response.AdjustmentLineResponse;
import com.ogc_prototype.ogc.dto.response.InventoryAdjustmentResponse;

import java.util.List;

public interface InventoryAdjustmentService {
    List<InventoryAdjustmentResponse> getAll();

    InventoryAdjustmentResponse getById(Integer id);

    InventoryAdjustmentResponse create(InventoryAdjustmentRequest request);

    AdjustmentLineResponse addLine(AdjustmentLineRequest request);

    List<AdjustmentLineResponse> getLines(Integer adjustmentId);

    AdjustmentLineResponse getLine(Integer adjustmentId, Integer lineId);
}

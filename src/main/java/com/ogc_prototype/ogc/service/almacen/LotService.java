package com.ogc_prototype.ogc.service.almacen;

import com.ogc_prototype.ogc.dto.request.LotRequest;
import com.ogc_prototype.ogc.dto.response.LotResponse;

import java.util.List;

public interface LotService {
    List<LotResponse> getAll();

    LotResponse getById(Integer id);

    List<LotResponse> getByProductId(Integer productId);

    LotResponse create(LotRequest request);
}

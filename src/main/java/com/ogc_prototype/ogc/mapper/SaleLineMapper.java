package com.ogc_prototype.ogc.mapper;

import com.ogc_prototype.ogc.dto.request.SaleLineRequest;
import com.ogc_prototype.ogc.dto.response.SaleLineResponse;
import com.ogc_prototype.ogc.model.Lot;
import com.ogc_prototype.ogc.model.Sale;
import com.ogc_prototype.ogc.model.SaleLine;

public class SaleLineMapper {

    private SaleLineMapper() {}

    public static SaleLineResponse toResponse(SaleLine line) {
        return SaleLineResponse.builder().id(line.getId()).saleId(line.getSale().getId())
                .lotId(line.getLot().getId()).productId(line.getLot().getProduct().getId())
                .productName(line.getLot().getProduct().getName()).weightKg(line.getWeightKg())
                .unitPricePerKg(line.getUnitPricePerKg()).build();
    }

    public static SaleLine toEntity(SaleLineRequest request, Sale sale, Lot lot) {
        return SaleLine.builder().sale(sale).lot(lot).weightKg(request.getWeightKg())
                .unitPricePerKg(request.getUnitPricePerKg()).build();
    }
}

package com.ogc_prototype.ogc.mapper;

import com.ogc_prototype.ogc.dto.request.PackRequest;
import com.ogc_prototype.ogc.dto.response.PackResponse;
import com.ogc_prototype.ogc.model.Pack;
import com.ogc_prototype.ogc.model.Product;

import java.util.List;

public class PackMapper {

    private PackMapper() {}

    public static PackResponse toResponse(Pack pack) {
        List<PackResponse.PackProductItem> items = pack.getProducts().stream()
                .map(p -> PackResponse.PackProductItem.builder().id(p.getId()).name(p.getName())
                        .cbdPercentage(p.getCbdPercentage()).thcPercentage(p.getThcPercentage())
                        .build())
                .toList();

        return PackResponse.builder().id(pack.getId()).name(pack.getName())
                .description(pack.getDescription()).price(pack.getPrice()).products(items)
                .active(pack.isActive()).build();
    }

    public static Pack toEntity(PackRequest request, List<Product> products) {
        return Pack.builder().name(request.getName()).description(request.getDescription())
                .price(request.getPrice()).products(products).active(request.isActive()).build();
    }
}

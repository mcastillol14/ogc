package com.ogc_prototype.ogc.mapper;

import com.ogc_prototype.ogc.dto.request.ProductRequest;
import com.ogc_prototype.ogc.dto.response.ProductResponse;
import com.ogc_prototype.ogc.model.Product;

public class ProductMapper {

    private ProductMapper() {}

    // el stock se calcula externamente sumando LotStock.remainingWeight del producto
    public static ProductResponse toResponse(Product product, Double stock) {
        return ProductResponse.builder().id(product.getId()).name(product.getName())
                .description(product.getDescription()).price(product.getPrice())
                .cbdPercentage(product.getCbdPercentage()).thcPercentage(product.getThcPercentage())
                .ohTenPercentage(product.getOhTenPercentage())
                .msPercentage(product.getMsPercentage())
                .nano10Percentage(product.getNano10Percentage())
                .deltaHcPercentage(product.getDeltaHcPercentage()).active(product.isActive())
                .stock(stock).createdAt(product.getCreatedAt()).updatedAt(product.getUpdatedAt())
                .build();
    }

    public static Product toEntity(ProductRequest request) {
        return Product.builder().name(request.getName()).description(request.getDescription())
                .price(request.getPrice()).cbdPercentage(request.getCbdPercentage())
                .thcPercentage(request.getThcPercentage())
                .ohTenPercentage(request.getOhTenPercentage())
                .msPercentage(request.getMsPercentage())
                .nano10Percentage(request.getNano10Percentage())
                .deltaHcPercentage(request.getDeltaHcPercentage()).active(request.isActive())
                .build();
    }
}

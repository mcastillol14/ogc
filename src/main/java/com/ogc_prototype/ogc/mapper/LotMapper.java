package com.ogc_prototype.ogc.mapper;

import com.ogc_prototype.ogc.dto.request.LotRequest;
import com.ogc_prototype.ogc.dto.response.LotResponse;
import com.ogc_prototype.ogc.model.Category;
import com.ogc_prototype.ogc.model.Lot;
import com.ogc_prototype.ogc.model.Product;
import com.ogc_prototype.ogc.model.Provider;

public class LotMapper {

    private LotMapper() {}

    // remainingWeight viene de LotStock
    public static LotResponse toResponse(Lot lot, Double remainingWeight) {
        return LotResponse.builder().id(lot.getId()).productId(lot.getProduct().getId())
                .productName(lot.getProduct().getName()).categoryId(lot.getCategory().getId())
                .categoryName(lot.getCategory().getName()).providerId(lot.getProvider().getId())
                .providerName(lot.getProvider().getName()).weight(lot.getWeight())
                .expirationDate(lot.getExpirationDate()).description(lot.getDescription())
                .remainingWeight(remainingWeight).createdAt(lot.getCreatedAt())
                .updatedAt(lot.getUpdatedAt()).build();
    }

    public static Lot toEntity(LotRequest request, Product product, Category category,
            Provider provider) {
        return Lot.builder().product(product).category(category).provider(provider)
                .weight(request.getWeight()).expirationDate(request.getExpirationDate())
                .description(request.getDescription()).build();
    }
}

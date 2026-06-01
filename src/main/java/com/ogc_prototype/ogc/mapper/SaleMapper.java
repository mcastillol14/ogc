package com.ogc_prototype.ogc.mapper;

import com.ogc_prototype.ogc.dto.request.SaleRequest;
import com.ogc_prototype.ogc.dto.response.SaleResponse;
import com.ogc_prototype.ogc.model.Customer;
import com.ogc_prototype.ogc.model.Sale;

public class SaleMapper {

    private SaleMapper() {}

    public static SaleResponse toResponse(Sale sale) {
        Customer customer = sale.getCustomer();
        return SaleResponse.builder().id(sale.getId()).customerId(customer.getId())
                .customerName(customer.getName() + " " + customer.getLastName())
                .saleDate(sale.getSaleDate()).totalAmount(sale.getTotalAmount())
                .notes(sale.getNotes()).discounted(sale.isDiscounted())
                .discountMode(sale.getDiscountMode()).discountValue(sale.getDiscountValue())
                .createdAt(sale.getCreatedAt())
                .updatedAt(sale.getUpdatedAt()).build();
    }

    public static Sale toEntity(SaleRequest request, Customer customer) {
        return Sale.builder().customer(customer).saleDate(request.getSaleDate())
                .notes(request.getNotes())
                .discounted(Boolean.TRUE.equals(request.getDiscounted()))
                .discountMode(request.getDiscountMode())
                .discountValue(request.getDiscountValue())
                .build();
    }
}

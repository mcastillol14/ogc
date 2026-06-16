package com.ogc_prototype.ogc.mapper;

import com.ogc_prototype.ogc.dto.request.SaleRequest;
import com.ogc_prototype.ogc.dto.response.SaleResponse;
import com.ogc_prototype.ogc.model.Customer;
import com.ogc_prototype.ogc.model.Sale;

public class SaleMapper {

    private SaleMapper() {}

    public static SaleResponse toResponse(Sale sale) {
        Customer customer = sale.getCustomer();
        Integer customerId = customer != null ? customer.getId() : null;
        String customerName =
                customer != null ? customer.getName() + " " + customer.getLastName() : null;
        return SaleResponse.builder().id(sale.getId()).customerId(customerId)
                .customerName(customerName).saleDate(sale.getSaleDate())
                .totalAmount(sale.getTotalAmount()).notes(sale.getNotes())
                .discounted(sale.isDiscounted()).discountMode(sale.getDiscountMode())
                .discountValue(sale.getDiscountValue()).build();
    }

    public static Sale toEntity(SaleRequest request, Customer customer) {
        return Sale.builder().customer(customer).saleDate(request.getSaleDate())
                .notes(request.getNotes()).discounted(Boolean.TRUE.equals(request.getDiscounted()))
                .discountMode(request.getDiscountMode()).discountValue(request.getDiscountValue())
                .build();
    }
}

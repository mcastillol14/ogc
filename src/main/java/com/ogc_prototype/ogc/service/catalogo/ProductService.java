package com.ogc_prototype.ogc.service.catalogo;

import com.ogc_prototype.ogc.dto.request.ProductRequest;
import com.ogc_prototype.ogc.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAll();

    List<ProductResponse> getAllActive();

    ProductResponse getById(Integer id);

    ProductResponse create(ProductRequest request);

    ProductResponse update(Integer id, ProductRequest request);

    ProductResponse activate(Integer id);

    ProductResponse deactivate(Integer id);
}

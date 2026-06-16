package com.ogc_prototype.ogc.service.catalogo;

import com.ogc_prototype.ogc.dto.request.PackRequest;
import com.ogc_prototype.ogc.dto.response.PackResponse;

import java.util.List;

public interface PackService {
    List<PackResponse> getAll();

    List<PackResponse> getAllActive();

    PackResponse getById(Integer id);

    PackResponse create(PackRequest request);

    PackResponse update(Integer id, PackRequest request);

    PackResponse activate(Integer id);

    PackResponse deactivate(Integer id);
}

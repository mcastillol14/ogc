package com.ogc_prototype.ogc.service.catalogo;

import com.ogc_prototype.ogc.dto.request.ProviderRequest;
import com.ogc_prototype.ogc.dto.response.ProviderResponse;

import java.util.List;

public interface ProviderService {
    List<ProviderResponse> getAll();

    List<ProviderResponse> getAllActive();

    ProviderResponse getById(Integer id);

    ProviderResponse create(ProviderRequest request);

    ProviderResponse update(Integer id, ProviderRequest request);

    ProviderResponse activate(Integer id);

    ProviderResponse deactivate(Integer id);
}

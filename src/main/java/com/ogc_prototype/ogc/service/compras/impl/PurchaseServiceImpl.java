package com.ogc_prototype.ogc.service.compras.impl;

import com.ogc_prototype.ogc.dto.request.PurchaseRequest;
import com.ogc_prototype.ogc.dto.response.PurchaseResponse;
import com.ogc_prototype.ogc.exception.ProviderException;
import com.ogc_prototype.ogc.exception.PurchaseException;
import com.ogc_prototype.ogc.mapper.PurchaseMapper;
import com.ogc_prototype.ogc.model.Provider;
import com.ogc_prototype.ogc.model.Purchase;
import com.ogc_prototype.ogc.repository.ProviderRepository;
import com.ogc_prototype.ogc.repository.PurchaseRepository;
import com.ogc_prototype.ogc.service.compras.PurchaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProviderRepository providerRepository;

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository,
            ProviderRepository providerRepository) {
        this.purchaseRepository = purchaseRepository;
        this.providerRepository = providerRepository;
    }

    @Override
    public List<PurchaseResponse> getAll() {
        return purchaseRepository.findAll().stream().map(PurchaseMapper::toResponse).toList();
    }

    @Override
    public PurchaseResponse getById(Integer id) {
        Purchase purchase =
                purchaseRepository.findById(id).orElseThrow(() -> PurchaseException.notFound(id));
        return PurchaseMapper.toResponse(purchase);
    }

    @Override
    @Transactional
    public PurchaseResponse create(PurchaseRequest request) {
        Provider provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> ProviderException.notFound(request.getProviderId()));
        if (!provider.isActive()) {
            throw ProviderException.inactive(request.getProviderId());
        }
        return PurchaseMapper
                .toResponse(purchaseRepository.save(PurchaseMapper.toEntity(request, provider)));
    }

    @Override
    @Transactional
    public PurchaseResponse update(Integer id, PurchaseRequest request) {
        Purchase purchase =
                purchaseRepository.findById(id).orElseThrow(() -> PurchaseException.notFound(id));
        Provider provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> ProviderException.notFound(request.getProviderId()));
        purchase.setProvider(provider);
        purchase.setExpectedDate(request.getExpectedDate());
        purchase.setNotes(request.getNotes());
        return PurchaseMapper.toResponse(purchaseRepository.save(purchase));
    }
}

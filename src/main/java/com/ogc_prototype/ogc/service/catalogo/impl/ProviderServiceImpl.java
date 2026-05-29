package com.ogc_prototype.ogc.service.catalogo.impl;

import com.ogc_prototype.ogc.dto.request.ProviderRequest;
import com.ogc_prototype.ogc.dto.response.ProviderResponse;
import com.ogc_prototype.ogc.exception.ProviderException;
import com.ogc_prototype.ogc.mapper.ProviderMapper;
import com.ogc_prototype.ogc.model.Provider;
import com.ogc_prototype.ogc.repository.ProviderRepository;
import com.ogc_prototype.ogc.service.catalogo.ProviderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;

    public ProviderServiceImpl(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Override
    public List<ProviderResponse> getAll() {
        return providerRepository.findAll().stream().map(ProviderMapper::toResponse).toList();
    }

    @Override
    public List<ProviderResponse> getAllActive() {
        return providerRepository.findAllByActiveTrue().stream().map(ProviderMapper::toResponse)
                .toList();
    }

    @Override
    public ProviderResponse getById(Integer id) {
        Provider provider =
                providerRepository.findById(id).orElseThrow(() -> ProviderException.notFound(id));
        return ProviderMapper.toResponse(provider);
    }

    @Override
    @Transactional
    public ProviderResponse create(ProviderRequest request) {
        if (providerRepository.existsByName(request.getName())) {
            throw ProviderException.duplicateName(request.getName());
        }
        if (providerRepository.existsByEmail(request.getEmail())) {
            throw ProviderException.duplicateEmail(request.getEmail());
        }
        if (providerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw ProviderException.duplicatePhone(request.getPhoneNumber());
        }
        return ProviderMapper.toResponse(providerRepository.save(ProviderMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public ProviderResponse update(Integer id, ProviderRequest request) {
        Provider provider =
                providerRepository.findById(id).orElseThrow(() -> ProviderException.notFound(id));
        if (!provider.getName().equals(request.getName())
                && providerRepository.existsByName(request.getName())) {
            throw ProviderException.duplicateName(request.getName());
        }
        if (!provider.getEmail().equals(request.getEmail())
                && providerRepository.existsByEmail(request.getEmail())) {
            throw ProviderException.duplicateEmail(request.getEmail());
        }
        if (!provider.getPhoneNumber().equals(request.getPhoneNumber())
                && providerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw ProviderException.duplicatePhone(request.getPhoneNumber());
        }
        provider.setName(request.getName());
        provider.setEmail(request.getEmail());
        provider.setPhoneNumber(request.getPhoneNumber());
        provider.setWebsite(request.getWebsite());
        provider.setNotes(request.getNotes());
        provider.setActive(request.isActive());
        return ProviderMapper.toResponse(providerRepository.save(provider));
    }

    @Override
    @Transactional
    public ProviderResponse activate(Integer id) {
        Provider provider =
                providerRepository.findById(id).orElseThrow(() -> ProviderException.notFound(id));
        provider.setActive(true);
        return ProviderMapper.toResponse(providerRepository.save(provider));
    }

    @Override
    @Transactional
    public ProviderResponse deactivate(Integer id) {
        Provider provider =
                providerRepository.findById(id).orElseThrow(() -> ProviderException.notFound(id));
        provider.setActive(false);
        return ProviderMapper.toResponse(providerRepository.save(provider));
    }
}

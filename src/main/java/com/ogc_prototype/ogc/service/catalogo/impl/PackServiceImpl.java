package com.ogc_prototype.ogc.service.catalogo.impl;

import com.ogc_prototype.ogc.dto.request.PackRequest;
import com.ogc_prototype.ogc.dto.response.PackResponse;
import com.ogc_prototype.ogc.exception.PackException;
import com.ogc_prototype.ogc.mapper.PackMapper;
import com.ogc_prototype.ogc.model.Pack;
import com.ogc_prototype.ogc.model.Product;
import com.ogc_prototype.ogc.repository.PackRepository;
import com.ogc_prototype.ogc.repository.ProductRepository;
import com.ogc_prototype.ogc.service.catalogo.PackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PackServiceImpl implements PackService {

    private final PackRepository packRepository;
    private final ProductRepository productRepository;

    public PackServiceImpl(PackRepository packRepository, ProductRepository productRepository) {
        this.packRepository = packRepository;
        this.productRepository = productRepository;
    }

    private List<Product> resolveProducts(List<Integer> productIds) {
        return productIds.stream().map(id -> productRepository.findById(id)
                .orElseThrow(() -> PackException.productNotFound(id))).toList();
    }

    @Override
    public List<PackResponse> getAll() {
        return packRepository.findAll().stream().map(PackMapper::toResponse).toList();
    }

    @Override
    public List<PackResponse> getAllActive() {
        return packRepository.findAllByActiveTrue().stream().map(PackMapper::toResponse).toList();
    }

    @Override
    public PackResponse getById(Integer id) {
        Pack pack = packRepository.findById(id).orElseThrow(() -> PackException.notFound(id));
        return PackMapper.toResponse(pack);
    }

    @Override
    @Transactional
    public PackResponse create(PackRequest request) {
        List<Product> products = resolveProducts(request.getProductIds());
        return PackMapper.toResponse(packRepository.save(PackMapper.toEntity(request, products)));
    }

    @Override
    @Transactional
    public PackResponse update(Integer id, PackRequest request) {
        Pack pack = packRepository.findById(id).orElseThrow(() -> PackException.notFound(id));
        pack.setName(request.getName());
        pack.setDescription(request.getDescription());
        pack.setPrice(request.getPrice());
        pack.setProducts(resolveProducts(request.getProductIds()));
        pack.setActive(request.isActive());
        return PackMapper.toResponse(packRepository.save(pack));
    }

    @Override
    @Transactional
    public PackResponse activate(Integer id) {
        Pack pack = packRepository.findById(id).orElseThrow(() -> PackException.notFound(id));
        pack.setActive(true);
        return PackMapper.toResponse(packRepository.save(pack));
    }

    @Override
    @Transactional
    public PackResponse deactivate(Integer id) {
        Pack pack = packRepository.findById(id).orElseThrow(() -> PackException.notFound(id));
        pack.setActive(false);
        return PackMapper.toResponse(packRepository.save(pack));
    }
}

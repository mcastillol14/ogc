package com.ogc_prototype.ogc.service.almacen.impl;

import com.ogc_prototype.ogc.dto.request.LotRequest;
import com.ogc_prototype.ogc.dto.response.LotResponse;
import com.ogc_prototype.ogc.exception.CategoryException;
import com.ogc_prototype.ogc.exception.LotException;
import com.ogc_prototype.ogc.exception.ProductException;
import com.ogc_prototype.ogc.exception.ProviderException;
import com.ogc_prototype.ogc.mapper.LotMapper;
import com.ogc_prototype.ogc.model.Category;
import com.ogc_prototype.ogc.model.Lot;
import com.ogc_prototype.ogc.model.LotStock;
import com.ogc_prototype.ogc.model.Product;
import com.ogc_prototype.ogc.model.Provider;
import com.ogc_prototype.ogc.repository.CategoryRepository;
import com.ogc_prototype.ogc.repository.LotRepository;
import com.ogc_prototype.ogc.repository.LotStockRepository;
import com.ogc_prototype.ogc.repository.ProductRepository;
import com.ogc_prototype.ogc.repository.ProviderRepository;
import com.ogc_prototype.ogc.service.almacen.LotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LotServiceImpl implements LotService {

    private final LotRepository lotRepository;
    private final LotStockRepository lotStockRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProviderRepository providerRepository;

    public LotServiceImpl(LotRepository lotRepository, LotStockRepository lotStockRepository,
            ProductRepository productRepository, CategoryRepository categoryRepository,
            ProviderRepository providerRepository) {
        this.lotRepository = lotRepository;
        this.lotStockRepository = lotStockRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.providerRepository = providerRepository;
    }

    private LotResponse toResponseWithStock(Lot lot) {
        Double remaining = lotStockRepository.findByLotId(lot.getId())
                .map(LotStock::getRemainingWeight).orElse(0.0);
        return LotMapper.toResponse(lot, remaining);
    }

    @Override
    public List<LotResponse> getAll() {
        return lotRepository.findAll().stream().map(this::toResponseWithStock).toList();
    }

    @Override
    public LotResponse getById(Integer id) {
        Lot lot = lotRepository.findById(id).orElseThrow(() -> LotException.notFound(id));
        return toResponseWithStock(lot);
    }

    @Override
    public List<LotResponse> getByProductId(Integer productId) {
        return lotRepository.findAllByProductId(productId).stream().map(this::toResponseWithStock)
                .toList();
    }

    @Override
    @Transactional
    public LotResponse create(LotRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> ProductException.notFound(request.getProductId()));
        if (!product.isActive()) {
            throw ProductException.inactive(request.getProductId());
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> CategoryException.notFound(request.getCategoryId()));
        if (!category.isActive()) {
            throw CategoryException.inactive(request.getCategoryId());
        }
        Provider provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> ProviderException.notFound(request.getProviderId()));
        if (!provider.isActive()) {
            throw ProviderException.inactive(request.getProviderId());
        }
        Lot lot = lotRepository.save(LotMapper.toEntity(request, product, category, provider));
        lotStockRepository
                .save(LotStock.builder().lot(lot).remainingWeight(lot.getWeight()).build());
        return LotMapper.toResponse(lot, lot.getWeight());
    }
}

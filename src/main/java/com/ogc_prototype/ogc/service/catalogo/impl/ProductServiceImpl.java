package com.ogc_prototype.ogc.service.catalogo.impl;

import com.ogc_prototype.ogc.dto.request.ProductRequest;
import com.ogc_prototype.ogc.dto.response.ProductResponse;
import com.ogc_prototype.ogc.exception.ProductException;
import com.ogc_prototype.ogc.mapper.ProductMapper;
import com.ogc_prototype.ogc.model.Product;
import com.ogc_prototype.ogc.repository.LotStockRepository;
import com.ogc_prototype.ogc.repository.ProductRepository;
import com.ogc_prototype.ogc.service.catalogo.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final LotStockRepository lotStockRepository;

    public ProductServiceImpl(ProductRepository productRepository,
            LotStockRepository lotStockRepository) {
        this.productRepository = productRepository;
        this.lotStockRepository = lotStockRepository;
    }

    private ProductResponse toResponseWithStock(Product product) {
        Double stock = lotStockRepository.sumRemainingWeightByProductId(product.getId());
        return ProductMapper.toResponse(product, stock);
    }

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream().map(this::toResponseWithStock).toList();
    }

    @Override
    public List<ProductResponse> getAllActive() {
        return productRepository.findAllByActiveTrue().stream().map(this::toResponseWithStock)
                .toList();
    }

    @Override
    public ProductResponse getById(Integer id) {
        Product product =
                productRepository.findById(id).orElseThrow(() -> ProductException.notFound(id));
        return toResponseWithStock(product);
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        return toResponseWithStock(productRepository.save(ProductMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public ProductResponse update(Integer id, ProductRequest request) {
        Product product =
                productRepository.findById(id).orElseThrow(() -> ProductException.notFound(id));
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCbdPercentage(request.getCbdPercentage());
        product.setThcPercentage(request.getThcPercentage());
        product.setOhTenPercentage(request.getOhTenPercentage());
        product.setMsPercentage(request.getMsPercentage());
        product.setNano10Percentage(request.getNano10Percentage());
        product.setDeltaHcPercentage(request.getDeltaHcPercentage());
        product.setActive(request.isActive());
        return toResponseWithStock(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse activate(Integer id) {
        Product product =
                productRepository.findById(id).orElseThrow(() -> ProductException.notFound(id));
        product.setActive(true);
        return toResponseWithStock(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse deactivate(Integer id) {
        Product product =
                productRepository.findById(id).orElseThrow(() -> ProductException.notFound(id));
        product.setActive(false);
        return toResponseWithStock(productRepository.save(product));
    }
}

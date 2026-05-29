package com.ogc_prototype.ogc.service.compras.impl;

import com.ogc_prototype.ogc.dto.request.PurchaseLineRequest;
import com.ogc_prototype.ogc.dto.response.PurchaseLineResponse;
import com.ogc_prototype.ogc.exception.LotException;
import com.ogc_prototype.ogc.exception.ProductException;
import com.ogc_prototype.ogc.exception.PurchaseException;
import com.ogc_prototype.ogc.mapper.PurchaseLineMapper;
import com.ogc_prototype.ogc.model.Lot;
import com.ogc_prototype.ogc.model.Product;
import com.ogc_prototype.ogc.model.Purchase;
import com.ogc_prototype.ogc.model.PurchaseLine;
import com.ogc_prototype.ogc.repository.LotRepository;
import com.ogc_prototype.ogc.repository.ProductRepository;
import com.ogc_prototype.ogc.repository.PurchaseLineRepository;
import com.ogc_prototype.ogc.repository.PurchaseRepository;
import com.ogc_prototype.ogc.service.compras.PurchaseLineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PurchaseLineServiceImpl implements PurchaseLineService {

    private final PurchaseLineRepository purchaseLineRepository;
    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final LotRepository lotRepository;

    public PurchaseLineServiceImpl(PurchaseLineRepository purchaseLineRepository,
            PurchaseRepository purchaseRepository, ProductRepository productRepository,
            LotRepository lotRepository) {
        this.purchaseLineRepository = purchaseLineRepository;
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
        this.lotRepository = lotRepository;
    }

    @Override
    public List<PurchaseLineResponse> getByPurchaseId(Integer purchaseId) {
        if (!purchaseRepository.existsById(purchaseId)) {
            throw PurchaseException.notFound(purchaseId);
        }
        return purchaseLineRepository.findAllByPurchaseId(purchaseId).stream()
                .map(PurchaseLineMapper::toResponse).toList();
    }

    @Override
    public PurchaseLineResponse getById(Integer purchaseId, Integer lineId) {
        PurchaseLine line = purchaseLineRepository.findByIdAndPurchaseId(lineId, purchaseId)
                .orElseThrow(() -> PurchaseException.lineNotFound(lineId));
        return PurchaseLineMapper.toResponse(line);
    }

    @Override
    @Transactional
    public PurchaseLineResponse create(PurchaseLineRequest request) {
        Purchase purchase = purchaseRepository.findById(request.getPurchaseId())
                .orElseThrow(() -> PurchaseException.notFound(request.getPurchaseId()));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> ProductException.notFound(request.getProductId()));
        if (!product.isActive()) {
            throw ProductException.inactive(request.getProductId());
        }
        PurchaseLine line = purchaseLineRepository
                .save(PurchaseLineMapper.toEntity(request, purchase, product));
        purchase.setTotalAmount(
                purchase.getTotalAmount() + line.getOrderedWeightKg() * line.getUnitPricePerKg());
        purchaseRepository.save(purchase);
        return PurchaseLineMapper.toResponse(line);
    }

    @Override
    @Transactional
    public PurchaseLineResponse assignLot(Integer purchaseId, Integer lineId, Integer lotId) {
        PurchaseLine line = purchaseLineRepository.findByIdAndPurchaseId(lineId, purchaseId)
                .orElseThrow(() -> PurchaseException.lineNotFound(lineId));
        Lot lot = lotRepository.findById(lotId).orElseThrow(() -> LotException.notFound(lotId));
        if (purchaseLineRepository.existsByLotIdAndIdNot(lotId, lineId)) {
            throw LotException.alreadyAssigned(lotId);
        }
        line.setLot(lot);
        return PurchaseLineMapper.toResponse(purchaseLineRepository.save(line));
    }

    @Override
    @Transactional
    public void delete(Integer purchaseId, Integer lineId) {
        PurchaseLine line = purchaseLineRepository.findByIdAndPurchaseId(lineId, purchaseId)
                .orElseThrow(() -> PurchaseException.lineNotFound(lineId));
        Purchase purchase = line.getPurchase();
        purchase.setTotalAmount(Math.max(0.0,
                purchase.getTotalAmount() - line.getOrderedWeightKg() * line.getUnitPricePerKg()));
        purchaseRepository.save(purchase);
        purchaseLineRepository.delete(line);
    }
}

package com.ogc_prototype.ogc.service.almacen.impl;

import com.ogc_prototype.ogc.dto.response.StockMovementResponse;
import com.ogc_prototype.ogc.mapper.StockMovementMapper;
import com.ogc_prototype.ogc.model.enums.MovementType;
import com.ogc_prototype.ogc.repository.StockMovementRepository;
import com.ogc_prototype.ogc.service.almacen.StockMovementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;

    public StockMovementServiceImpl(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    @Override
    public List<StockMovementResponse> getAll() {
        return stockMovementRepository.findAll().stream().map(StockMovementMapper::toResponse)
                .toList();
    }

    @Override
    public List<StockMovementResponse> getByLotId(Integer lotId) {
        return stockMovementRepository.findAllByLotId(lotId).stream()
                .map(StockMovementMapper::toResponse).toList();
    }

    @Override
    public List<StockMovementResponse> getByPurchaseId(Integer purchaseId) {
        return stockMovementRepository.findAllByPurchaseId(purchaseId).stream()
                .map(StockMovementMapper::toResponse).toList();
    }

    @Override
    public List<StockMovementResponse> getBySaleId(Integer saleId) {
        return stockMovementRepository.findAllBySaleId(saleId).stream()
                .map(StockMovementMapper::toResponse).toList();
    }

    @Override
    public List<StockMovementResponse> getByType(MovementType type) {
        return stockMovementRepository.findAllByType(type).stream()
                .map(StockMovementMapper::toResponse).toList();
    }
}

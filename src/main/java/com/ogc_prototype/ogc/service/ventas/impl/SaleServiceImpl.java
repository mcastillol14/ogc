package com.ogc_prototype.ogc.service.ventas.impl;

import com.ogc_prototype.ogc.dto.request.SaleRequest;
import com.ogc_prototype.ogc.dto.response.SaleResponse;
import com.ogc_prototype.ogc.exception.CustomerException;
import com.ogc_prototype.ogc.exception.SaleException;
import com.ogc_prototype.ogc.mapper.SaleMapper;
import com.ogc_prototype.ogc.model.Customer;
import com.ogc_prototype.ogc.model.Sale;
import com.ogc_prototype.ogc.repository.CustomerRepository;
import com.ogc_prototype.ogc.repository.SaleRepository;
import com.ogc_prototype.ogc.service.ventas.SaleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;

    public SaleServiceImpl(SaleRepository saleRepository, CustomerRepository customerRepository) {
        this.saleRepository = saleRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<SaleResponse> getAll() {
        return saleRepository.findAll().stream().map(SaleMapper::toResponse).toList();
    }

    @Override
    public SaleResponse getById(Integer id) {
        Sale sale = saleRepository.findById(id).orElseThrow(() -> SaleException.notFound(id));
        return SaleMapper.toResponse(sale);
    }

    @Override
    @Transactional
    public SaleResponse create(SaleRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> CustomerException.notFound(request.getCustomerId()));
        return SaleMapper.toResponse(saleRepository.save(SaleMapper.toEntity(request, customer)));
    }

    @Override
    @Transactional
    public SaleResponse update(Integer id, SaleRequest request) {
        Sale sale = saleRepository.findById(id).orElseThrow(() -> SaleException.notFound(id));
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> CustomerException.notFound(request.getCustomerId()));
        sale.setCustomer(customer);
        sale.setSaleDate(request.getSaleDate());
        sale.setNotes(request.getNotes());
        return SaleMapper.toResponse(saleRepository.save(sale));
    }
}

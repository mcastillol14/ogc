package com.ogc_prototype.ogc.service.ventas;

import com.ogc_prototype.ogc.dto.request.SaleRequest;
import com.ogc_prototype.ogc.dto.response.SaleResponse;
import com.ogc_prototype.ogc.exception.CustomerException;
import com.ogc_prototype.ogc.exception.SaleException;
import com.ogc_prototype.ogc.model.Customer;
import com.ogc_prototype.ogc.model.Sale;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.repository.CustomerRepository;
import com.ogc_prototype.ogc.repository.SaleRepository;
import com.ogc_prototype.ogc.service.ventas.impl.SaleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleServiceImplTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private SaleServiceImpl service;

    private Customer buildCustomer(int id) {
        return Customer.builder().id(id).name("Alice").lastName("Smith").email("alice@example.com")
                .userName("alice").password("hash").role(Role.CUSTOMER).phoneNumber("+34600000001")
                .address("Calle 1").city("Madrid").zipCode("28001").country("ES").build();
    }

    private Sale buildSale(int id, Customer customer) {
        return Sale.builder().id(id).customer(customer).saleDate(LocalDate.now()).totalAmount(0.0)
                .build();
    }

    @Test
    void getAll_returnsList() {
        Customer customer = buildCustomer(1);
        when(saleRepository.findAll()).thenReturn(List.of(buildSale(1, customer)));

        List<SaleResponse> result = service.getAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void getById_found_returnsResponse() {
        Customer customer = buildCustomer(1);
        when(saleRepository.findById(1)).thenReturn(Optional.of(buildSale(1, customer)));

        SaleResponse response = service.getById(1);

        assertThat(response.getId()).isEqualTo(1);
    }

    @Test
    void getById_notFound_throwsSaleException() {
        when(saleRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99)).isInstanceOf(SaleException.class);
    }

    @Test
    void create_existingCustomer_savesAndReturns() {
        Customer customer = buildCustomer(1);
        SaleRequest req = SaleRequest.builder().customerId(1).saleDate(LocalDate.now()).build();
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(saleRepository.save(any())).thenReturn(buildSale(1, customer));

        SaleResponse response = service.create(req);

        assertThat(response).isNotNull();
        verify(saleRepository).save(any());
    }

    @Test
    void create_customerNotFound_throwsCustomerException() {
        SaleRequest req = SaleRequest.builder().customerId(99).saleDate(LocalDate.now()).build();
        when(customerRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req)).isInstanceOf(CustomerException.class);
    }

    @Test
    void update_existingSale_updatesAndReturns() {
        Customer customer = buildCustomer(1);
        Sale sale = buildSale(1, customer);
        SaleRequest req = SaleRequest.builder().customerId(1).saleDate(LocalDate.now()).build();
        when(saleRepository.findById(1)).thenReturn(Optional.of(sale));
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(saleRepository.save(any())).thenReturn(sale);

        SaleResponse response = service.update(1, req);

        assertThat(response).isNotNull();
        verify(saleRepository).save(any());
    }

    @Test
    void update_notFound_throwsSaleException() {
        when(saleRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99,
                SaleRequest.builder().customerId(1).saleDate(LocalDate.now()).build()))
                        .isInstanceOf(SaleException.class);
    }
}

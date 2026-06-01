package com.ogc_prototype.ogc.service.compras;

import com.ogc_prototype.ogc.dto.request.PurchaseRequest;
import com.ogc_prototype.ogc.dto.response.PurchaseResponse;
import com.ogc_prototype.ogc.exception.ProviderException;
import com.ogc_prototype.ogc.exception.PurchaseException;
import com.ogc_prototype.ogc.model.Provider;
import com.ogc_prototype.ogc.model.Purchase;
import com.ogc_prototype.ogc.repository.ProviderRepository;
import com.ogc_prototype.ogc.repository.PurchaseRepository;
import com.ogc_prototype.ogc.service.compras.impl.PurchaseServiceImpl;
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
class PurchaseServiceImplTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private PurchaseServiceImpl service;

    private Provider activeProvider() {
        return Provider.builder().id(1).name("Proveedor SA").email("p@p.com")
                .phoneNumber("+34600000001").active(true).build();
    }

    private Purchase buildPurchase(int id) {
        return Purchase.builder().id(id).provider(activeProvider()).totalAmount(0.0)
                .expectedDate(LocalDate.now()).build();
    }

    @Test
    void getAll_returnsList() {
        when(purchaseRepository.findAll()).thenReturn(List.of(buildPurchase(1)));

        List<PurchaseResponse> result = service.getAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void getById_found_returnsResponse() {
        when(purchaseRepository.findById(1)).thenReturn(Optional.of(buildPurchase(1)));

        PurchaseResponse response = service.getById(1);

        assertThat(response.getId()).isEqualTo(1);
    }

    @Test
    void getById_notFound_throwsPurchaseException() {
        when(purchaseRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99)).isInstanceOf(PurchaseException.class);
    }

    @Test
    void create_activeProvider_savesAndReturns() {
        PurchaseRequest req = PurchaseRequest.builder().providerId(1).expectedDate(LocalDate.now())
                .notes("notas").build();
        when(providerRepository.findById(1)).thenReturn(Optional.of(activeProvider()));
        when(purchaseRepository.save(any())).thenReturn(buildPurchase(1));

        PurchaseResponse response = service.create(req);

        assertThat(response).isNotNull();
        verify(purchaseRepository).save(any());
    }

    @Test
    void create_inactiveProvider_throwsProviderException() {
        Provider inactive = Provider.builder().id(1).name("P").email("p@p.com")
                .phoneNumber("+34600000001").active(false).build();
        PurchaseRequest req =
                PurchaseRequest.builder().providerId(1).expectedDate(LocalDate.now()).build();
        when(providerRepository.findById(1)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> service.create(req)).isInstanceOf(ProviderException.class);
    }

    @Test
    void create_providerNotFound_throwsProviderException() {
        PurchaseRequest req =
                PurchaseRequest.builder().providerId(99).expectedDate(LocalDate.now()).build();
        when(providerRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req)).isInstanceOf(ProviderException.class);
    }

    @Test
    void update_existingPurchase_updatesAndReturns() {
        Purchase purchase = buildPurchase(1);
        PurchaseRequest req =
                PurchaseRequest.builder().providerId(1).expectedDate(LocalDate.now()).build();
        when(purchaseRepository.findById(1)).thenReturn(Optional.of(purchase));
        when(providerRepository.findById(1)).thenReturn(Optional.of(activeProvider()));
        when(purchaseRepository.save(any())).thenReturn(purchase);

        PurchaseResponse response = service.update(1, req);

        assertThat(response).isNotNull();
        verify(purchaseRepository).save(any());
    }

    @Test
    void update_notFound_throwsPurchaseException() {
        when(purchaseRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.update(99, PurchaseRequest.builder().providerId(1).build()))
                        .isInstanceOf(PurchaseException.class);
    }
}

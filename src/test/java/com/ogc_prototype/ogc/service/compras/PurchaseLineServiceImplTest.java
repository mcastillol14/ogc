package com.ogc_prototype.ogc.service.compras;

import com.ogc_prototype.ogc.dto.request.PurchaseLineRequest;
import com.ogc_prototype.ogc.dto.response.PurchaseLineResponse;
import com.ogc_prototype.ogc.exception.LotException;
import com.ogc_prototype.ogc.exception.ProductException;
import com.ogc_prototype.ogc.exception.PurchaseException;
import com.ogc_prototype.ogc.model.Lot;
import com.ogc_prototype.ogc.model.Product;
import com.ogc_prototype.ogc.model.Provider;
import com.ogc_prototype.ogc.model.Purchase;
import com.ogc_prototype.ogc.model.PurchaseLine;
import com.ogc_prototype.ogc.repository.LotRepository;
import com.ogc_prototype.ogc.repository.ProductRepository;
import com.ogc_prototype.ogc.repository.PurchaseLineRepository;
import com.ogc_prototype.ogc.repository.PurchaseRepository;
import com.ogc_prototype.ogc.service.compras.impl.PurchaseLineServiceImpl;
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
class PurchaseLineServiceImplTest {

    @Mock
    private PurchaseLineRepository purchaseLineRepository;
    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private LotRepository lotRepository;

    @InjectMocks
    private PurchaseLineServiceImpl service;

    private Purchase buildPurchase(int id) {
        Provider provider = Provider.builder().id(1).name("P").email("p@p.com")
                .phoneNumber("+34600000001").active(true).build();
        return Purchase.builder().id(id).provider(provider).totalAmount(0.0)
                .expectedDate(LocalDate.now()).build();
    }

    private Product activeProduct() {
        return Product.builder().id(1).name("OG Kush").price(25.0).cbdPercentage(10.0)
                .thcPercentage(0.2).active(true).build();
    }

    private PurchaseLine buildLine(int id, Purchase purchase) {
        return PurchaseLine.builder().id(id).purchase(purchase).product(activeProduct())
                .orderedWeightKg(10.0).unitPricePerKg(25.0).build();
    }

    @Test
    void getByPurchaseId_purchaseNotFound_throwsPurchaseException() {
        when(purchaseRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> service.getByPurchaseId(99)).isInstanceOf(PurchaseException.class);
    }

    @Test
    void getByPurchaseId_found_returnsList() {
        Purchase purchase = buildPurchase(1);
        when(purchaseRepository.existsById(1)).thenReturn(true);
        when(purchaseLineRepository.findAllByPurchaseId(1))
                .thenReturn(List.of(buildLine(1, purchase)));

        List<PurchaseLineResponse> result = service.getByPurchaseId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void getById_found_returnsLine() {
        Purchase purchase = buildPurchase(1);
        when(purchaseLineRepository.findByIdAndPurchaseId(1, 1))
                .thenReturn(Optional.of(buildLine(1, purchase)));

        PurchaseLineResponse response = service.getById(1, 1);

        assertThat(response).isNotNull();
    }

    @Test
    void getById_notFound_throwsPurchaseException() {
        when(purchaseLineRepository.findByIdAndPurchaseId(99, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(1, 99)).isInstanceOf(PurchaseException.class);
    }

    @Test
    void create_activeProduct_savesLineAndUpdatesPurchaseTotal() {
        Purchase purchase = buildPurchase(1);
        PurchaseLine savedLine = buildLine(1, purchase);
        PurchaseLineRequest req = PurchaseLineRequest.builder().purchaseId(1).productId(1)
                .orderedWeightKg(10.0).unitPricePerKg(25.0).build();
        when(purchaseRepository.findById(1)).thenReturn(Optional.of(purchase));
        when(productRepository.findById(1)).thenReturn(Optional.of(activeProduct()));
        when(purchaseLineRepository.save(any())).thenReturn(savedLine);
        when(purchaseRepository.save(any())).thenReturn(purchase);

        PurchaseLineResponse response = service.create(req);

        assertThat(response).isNotNull();
        assertThat(purchase.getTotalAmount()).isEqualTo(250.0);
    }

    @Test
    void create_inactiveProduct_throwsProductException() {
        Product inactive = Product.builder().id(1).name("X").price(1.0).cbdPercentage(0.0)
                .thcPercentage(0.0).active(false).build();
        PurchaseLineRequest req = PurchaseLineRequest.builder().purchaseId(1).productId(1)
                .orderedWeightKg(5.0).unitPricePerKg(10.0).build();
        when(purchaseRepository.findById(1)).thenReturn(Optional.of(buildPurchase(1)));
        when(productRepository.findById(1)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> service.create(req)).isInstanceOf(ProductException.class);
    }

    @Test
    void assignLot_lotAlreadyAssigned_throwsLotException() {
        Purchase purchase = buildPurchase(1);
        PurchaseLine line = buildLine(1, purchase);
        Lot lot = Lot.builder().id(5).weight(50.0).build();
        when(purchaseLineRepository.findByIdAndPurchaseId(1, 1)).thenReturn(Optional.of(line));
        when(lotRepository.findById(5)).thenReturn(Optional.of(lot));
        when(purchaseLineRepository.existsByLotIdAndIdNot(5, 1)).thenReturn(true);

        assertThatThrownBy(() -> service.assignLot(1, 1, 5)).isInstanceOf(LotException.class);
    }

    @Test
    void assignLot_available_assignsAndReturns() {
        Purchase purchase = buildPurchase(1);
        PurchaseLine line = buildLine(1, purchase);
        Lot lot = Lot.builder().id(5).weight(50.0).build();
        when(purchaseLineRepository.findByIdAndPurchaseId(1, 1)).thenReturn(Optional.of(line));
        when(lotRepository.findById(5)).thenReturn(Optional.of(lot));
        when(purchaseLineRepository.existsByLotIdAndIdNot(5, 1)).thenReturn(false);
        when(purchaseLineRepository.save(any())).thenReturn(line);

        PurchaseLineResponse response = service.assignLot(1, 1, 5);

        assertThat(response).isNotNull();
        verify(purchaseLineRepository).save(line);
    }

    @Test
    void delete_existingLine_decreasesTotalAndDeletes() {
        Purchase purchase = buildPurchase(1);
        purchase.setTotalAmount(250.0);
        PurchaseLine line = buildLine(1, purchase);
        when(purchaseLineRepository.findByIdAndPurchaseId(1, 1)).thenReturn(Optional.of(line));
        when(purchaseRepository.save(any())).thenReturn(purchase);

        service.delete(1, 1);

        assertThat(purchase.getTotalAmount()).isEqualTo(0.0);
        verify(purchaseLineRepository).delete(line);
    }
}

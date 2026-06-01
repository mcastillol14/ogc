package com.ogc_prototype.ogc.service.almacen;

import com.ogc_prototype.ogc.dto.request.LotRequest;
import com.ogc_prototype.ogc.dto.response.LotResponse;
import com.ogc_prototype.ogc.exception.CategoryException;
import com.ogc_prototype.ogc.exception.LotException;
import com.ogc_prototype.ogc.exception.ProductException;
import com.ogc_prototype.ogc.exception.ProviderException;
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
import com.ogc_prototype.ogc.service.almacen.impl.LotServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LotServiceImplTest {

    @Mock
    private LotRepository lotRepository;
    @Mock
    private LotStockRepository lotStockRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private LotServiceImpl service;

    private Product activeProduct() {
        return Product.builder().id(1).name("OG Kush").price(25.0).cbdPercentage(10.0)
                .thcPercentage(0.2).active(true).build();
    }

    private Category activeCategory() {
        return Category.builder().id(1).name("Indica").slug("indica").active(true).build();
    }

    private Provider activeProvider() {
        return Provider.builder().id(1).name("Proveedor SA").email("p@p.com")
                .phoneNumber("+34600000001").active(true).build();
    }

    private Lot buildLot(int id) {
        return Lot.builder().id(id).product(activeProduct()).category(activeCategory())
                .provider(activeProvider()).weight(100.0)
                .expirationDate(new Date(System.currentTimeMillis() + 86400000L)).build();
    }

    @Test
    void getAll_returnsList() {
        when(lotRepository.findAll()).thenReturn(List.of(buildLot(1)));
        when(lotStockRepository.findByLotId(1)).thenReturn(
                Optional.of(LotStock.builder().lot(buildLot(1)).remainingWeight(100.0).build()));

        List<LotResponse> result = service.getAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void getById_found_returnsResponse() {
        when(lotRepository.findById(1)).thenReturn(Optional.of(buildLot(1)));
        when(lotStockRepository.findByLotId(1)).thenReturn(
                Optional.of(LotStock.builder().lot(buildLot(1)).remainingWeight(100.0).build()));

        LotResponse response = service.getById(1);

        assertThat(response.getId()).isEqualTo(1);
    }

    @Test
    void getById_notFound_throwsLotException() {
        when(lotRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99)).isInstanceOf(LotException.class);
    }

    @Test
    void getByProductId_returnsList() {
        Lot lot = buildLot(1);
        when(lotRepository.findAllByProductId(1)).thenReturn(List.of(lot));
        when(lotStockRepository.findByLotId(1)).thenReturn(Optional.empty());

        List<LotResponse> result = service.getByProductId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void create_allActive_savesLotAndStock() {
        LotRequest req = LotRequest.builder().productId(1).categoryId(1).providerId(1).weight(50.0)
                .expirationDate(new Date(System.currentTimeMillis() + 86400000L)).build();
        Lot saved = buildLot(2);

        when(productRepository.findById(1)).thenReturn(Optional.of(activeProduct()));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(activeCategory()));
        when(providerRepository.findById(1)).thenReturn(Optional.of(activeProvider()));
        when(lotRepository.save(any())).thenReturn(saved);
        when(lotStockRepository.save(any()))
                .thenReturn(LotStock.builder().lot(saved).remainingWeight(50.0).build());

        LotResponse response = service.create(req);

        assertThat(response).isNotNull();
        verify(lotRepository).save(any());
        verify(lotStockRepository).save(any());
    }

    @Test
    void create_inactiveProduct_throwsProductException() {
        Product inactiveProduct = Product.builder().id(1).name("X").price(1.0).cbdPercentage(0.0)
                .thcPercentage(0.0).active(false).build();
        LotRequest req = LotRequest.builder().productId(1).categoryId(1).providerId(1).weight(10.0)
                .expirationDate(new Date(System.currentTimeMillis() + 86400000L)).build();
        when(productRepository.findById(1)).thenReturn(Optional.of(inactiveProduct));

        assertThatThrownBy(() -> service.create(req)).isInstanceOf(ProductException.class);
    }

    @Test
    void create_inactiveCategory_throwsCategoryException() {
        Category inactiveCategory =
                Category.builder().id(1).name("X").slug("x").active(false).build();
        LotRequest req = LotRequest.builder().productId(1).categoryId(1).providerId(1).weight(10.0)
                .expirationDate(new Date(System.currentTimeMillis() + 86400000L)).build();
        when(productRepository.findById(1)).thenReturn(Optional.of(activeProduct()));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(inactiveCategory));

        assertThatThrownBy(() -> service.create(req)).isInstanceOf(CategoryException.class);
    }

    @Test
    void create_inactiveProvider_throwsProviderException() {
        Provider inactiveProvider = Provider.builder().id(1).name("P").email("p@p.com")
                .phoneNumber("+34600000001").active(false).build();
        LotRequest req = LotRequest.builder().productId(1).categoryId(1).providerId(1).weight(10.0)
                .expirationDate(new Date(System.currentTimeMillis() + 86400000L)).build();
        when(productRepository.findById(1)).thenReturn(Optional.of(activeProduct()));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(activeCategory()));
        when(providerRepository.findById(1)).thenReturn(Optional.of(inactiveProvider));

        assertThatThrownBy(() -> service.create(req)).isInstanceOf(ProviderException.class);
    }
}

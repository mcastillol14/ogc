package com.ogc_prototype.ogc.service.catalogo;

import com.ogc_prototype.ogc.dto.request.ProductRequest;
import com.ogc_prototype.ogc.dto.response.ProductResponse;
import com.ogc_prototype.ogc.exception.ProductException;
import com.ogc_prototype.ogc.model.Product;
import com.ogc_prototype.ogc.repository.LotStockRepository;
import com.ogc_prototype.ogc.repository.ProductRepository;
import com.ogc_prototype.ogc.service.catalogo.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private LotStockRepository lotStockRepository;

    @InjectMocks
    private ProductServiceImpl service;

    private Product buildProduct(int id, boolean active) {
        return Product.builder().id(id).name("OG Kush").description("desc").price(25.0)
                .cbdPercentage(10.0).thcPercentage(0.2).active(active).build();
    }

    @Test
    void getAll_returnsAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(buildProduct(1, true)));
        when(lotStockRepository.sumRemainingWeightByProductId(1)).thenReturn(50.0);

        List<ProductResponse> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("OG Kush");
    }

    @Test
    void getAllActive_returnsOnlyActiveProducts() {
        when(productRepository.findAllByActiveTrue()).thenReturn(List.of(buildProduct(1, true)));
        when(lotStockRepository.sumRemainingWeightByProductId(anyInt())).thenReturn(0.0);

        List<ProductResponse> result = service.getAllActive();

        assertThat(result).hasSize(1);
    }

    @Test
    void getById_existingId_returnsProduct() {
        when(productRepository.findById(1)).thenReturn(Optional.of(buildProduct(1, true)));
        when(lotStockRepository.sumRemainingWeightByProductId(1)).thenReturn(10.0);

        ProductResponse response = service.getById(1);

        assertThat(response.getId()).isEqualTo(1);
    }

    @Test
    void getById_nonExistingId_throwsProductException() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99)).isInstanceOf(ProductException.class);
    }

    @Test
    void create_savesProductAndReturnsResponse() {
        ProductRequest request = ProductRequest.builder().name("Blue Dream").description("d")
                .price(30.0).cbdPercentage(15.0).thcPercentage(0.2).active(true).build();
        Product saved = buildProduct(2, true);
        when(productRepository.save(any())).thenReturn(saved);
        when(lotStockRepository.sumRemainingWeightByProductId(2)).thenReturn(0.0);

        ProductResponse response = service.create(request);

        assertThat(response).isNotNull();
        verify(productRepository).save(any());
    }

    @Test
    void activate_setsActiveTrueAndSaves() {
        Product product = buildProduct(1, false);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(lotStockRepository.sumRemainingWeightByProductId(1)).thenReturn(0.0);

        ProductResponse response = service.activate(1);

        assertThat(response.isActive()).isTrue();
        verify(productRepository).save(product);
    }

    @Test
    void deactivate_setsActiveFalseAndSaves() {
        Product product = buildProduct(1, true);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(lotStockRepository.sumRemainingWeightByProductId(1)).thenReturn(0.0);

        ProductResponse response = service.deactivate(1);

        assertThat(response.isActive()).isFalse();
        verify(productRepository).save(product);
    }
}

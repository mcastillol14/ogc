package com.ogc_prototype.ogc.service.catalogo;

import com.ogc_prototype.ogc.dto.request.PackRequest;
import com.ogc_prototype.ogc.dto.response.PackResponse;
import com.ogc_prototype.ogc.exception.PackException;
import com.ogc_prototype.ogc.model.Pack;
import com.ogc_prototype.ogc.model.Product;
import com.ogc_prototype.ogc.repository.PackRepository;
import com.ogc_prototype.ogc.repository.ProductRepository;
import com.ogc_prototype.ogc.service.catalogo.impl.PackServiceImpl;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PackServiceImplTest {

    @Mock
    private PackRepository packRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PackServiceImpl service;

    private Product buildProduct(int id) {
        return Product.builder().id(id).name("Producto " + id).description("desc").price(20.0)
                .cbdPercentage(10.0).thcPercentage(0.2).ohTenPercentage(0.0).msPercentage(0.0)
                .nano10Percentage(0.0).deltaHcPercentage(0.0).active(true).build();
    }

    private Pack buildPack(int id, boolean active) {
        return Pack.builder().id(id).name("Pack Test").description("desc pack").price(49.99)
                .products(List.of(buildProduct(1), buildProduct(2))).active(active).build();
    }

    private PackRequest buildRequest() {
        return PackRequest.builder().name("Pack Premium").description("Descripción del pack")
                .price(59.99).productIds(List.of(1, 2)).active(true).build();
    }

    // ── getAll ──────────────────────────────────────────────────────────────

    @Test
    void getAll_returnsAllPacks() {
        when(packRepository.findAll()).thenReturn(List.of(buildPack(1, true), buildPack(2, false)));

        List<PackResponse> result = service.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Pack Test");
    }

    // ── getAllActive ─────────────────────────────────────────────────────────

    @Test
    void getAllActive_returnsOnlyActivePacks() {
        when(packRepository.findAllByActiveTrue()).thenReturn(List.of(buildPack(1, true)));

        List<PackResponse> result = service.getAllActive();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();
    }

    // ── getById ──────────────────────────────────────────────────────────────

    @Test
    void getById_existingId_returnsPack() {
        when(packRepository.findById(1)).thenReturn(Optional.of(buildPack(1, true)));

        PackResponse response = service.getById(1);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getPrice()).isEqualTo(49.99);
        assertThat(response.getProducts()).hasSize(2);
    }

    @Test
    void getById_nonExistingId_throwsPackException() {
        when(packRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99)).isInstanceOf(PackException.class);
    }

    // ── create ───────────────────────────────────────────────────────────────

    @Test
    void create_savesPackAndReturnsResponse() {
        PackRequest request = buildRequest();
        when(productRepository.findById(1)).thenReturn(Optional.of(buildProduct(1)));
        when(productRepository.findById(2)).thenReturn(Optional.of(buildProduct(2)));
        Pack saved = buildPack(3, true);
        when(packRepository.save(any())).thenReturn(saved);

        PackResponse response = service.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getProducts()).hasSize(2);
        verify(packRepository).save(any());
    }

    @Test
    void create_withNonExistingProduct_throwsPackException() {
        PackRequest request = buildRequest(); // productIds = [1, 2]
        when(productRepository.findById(1)).thenReturn(Optional.of(buildProduct(1)));
        when(productRepository.findById(2)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(PackException.class);
        verify(packRepository, never()).save(any());
    }

    // ── update ───────────────────────────────────────────────────────────────

    @Test
    void update_existingPack_updatesFieldsAndSaves() {
        Pack existing = buildPack(1, true);
        PackRequest request = PackRequest.builder().name("Pack Actualizado")
                .description("Nueva desc").price(75.0).productIds(List.of(1)).active(true).build();
        when(packRepository.findById(1)).thenReturn(Optional.of(existing));
        when(productRepository.findById(1)).thenReturn(Optional.of(buildProduct(1)));
        when(packRepository.save(existing)).thenReturn(existing);

        PackResponse response = service.update(1, request);

        assertThat(response.getName()).isEqualTo("Pack Actualizado");
        assertThat(response.getPrice()).isEqualTo(75.0);
        verify(packRepository).save(existing);
    }

    @Test
    void update_nonExistingPack_throwsPackException() {
        when(packRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, buildRequest()))
                .isInstanceOf(PackException.class);
        verify(packRepository, never()).save(any());
    }

    // ── activate ─────────────────────────────────────────────────────────────

    @Test
    void activate_setsActiveTrueAndSaves() {
        Pack pack = buildPack(1, false);
        when(packRepository.findById(1)).thenReturn(Optional.of(pack));
        when(packRepository.save(pack)).thenReturn(pack);

        PackResponse response = service.activate(1);

        assertThat(response.isActive()).isTrue();
        verify(packRepository).save(pack);
    }

    @Test
    void activate_nonExistingPack_throwsPackException() {
        when(packRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.activate(99)).isInstanceOf(PackException.class);
        verify(packRepository, never()).save(any());
    }

    // ── deactivate ───────────────────────────────────────────────────────────

    @Test
    void deactivate_setsActiveFalseAndSaves() {
        Pack pack = buildPack(1, true);
        when(packRepository.findById(1)).thenReturn(Optional.of(pack));
        when(packRepository.save(pack)).thenReturn(pack);

        PackResponse response = service.deactivate(1);

        assertThat(response.isActive()).isFalse();
        verify(packRepository).save(pack);
    }

    @Test
    void deactivate_nonExistingPack_throwsPackException() {
        when(packRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deactivate(99)).isInstanceOf(PackException.class);
        verify(packRepository, never()).save(any());
    }
}

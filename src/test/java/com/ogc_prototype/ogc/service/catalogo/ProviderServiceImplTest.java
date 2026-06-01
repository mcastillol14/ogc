package com.ogc_prototype.ogc.service.catalogo;

import com.ogc_prototype.ogc.dto.request.ProviderRequest;
import com.ogc_prototype.ogc.dto.response.ProviderResponse;
import com.ogc_prototype.ogc.exception.ProviderException;
import com.ogc_prototype.ogc.model.Provider;
import com.ogc_prototype.ogc.repository.ProviderRepository;
import com.ogc_prototype.ogc.service.catalogo.impl.ProviderServiceImpl;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProviderServiceImplTest {

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProviderServiceImpl service;

    private Provider buildProvider(int id, boolean active) {
        return Provider.builder().id(id).name("Proveedor SA").email("prov@example.com")
                .phoneNumber("+34600000001").active(active).build();
    }

    @Test
    void getAll_returnsList() {
        when(providerRepository.findAll()).thenReturn(List.of(buildProvider(1, true)));

        assertThat(service.getAll()).hasSize(1);
    }

    @Test
    void getAllActive_returnsOnlyActive() {
        when(providerRepository.findAllByActiveTrue()).thenReturn(List.of(buildProvider(1, true)));

        assertThat(service.getAllActive()).hasSize(1);
    }

    @Test
    void getById_found_returnsResponse() {
        when(providerRepository.findById(1)).thenReturn(Optional.of(buildProvider(1, true)));

        ProviderResponse response = service.getById(1);

        assertThat(response.getId()).isEqualTo(1);
    }

    @Test
    void getById_notFound_throwsProviderException() {
        when(providerRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99)).isInstanceOf(ProviderException.class);
    }

    @Test
    void create_allUnique_savesAndReturns() {
        ProviderRequest req = ProviderRequest.builder().name("Nuevo").email("new@example.com")
                .phoneNumber("+34600000002").active(true).build();
        when(providerRepository.existsByName("Nuevo")).thenReturn(false);
        when(providerRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(providerRepository.existsByPhoneNumber("+34600000002")).thenReturn(false);
        when(providerRepository.save(any())).thenReturn(buildProvider(2, true));

        ProviderResponse response = service.create(req);

        assertThat(response).isNotNull();
        verify(providerRepository).save(any());
    }

    @Test
    void create_duplicateName_throwsProviderException() {
        ProviderRequest req = ProviderRequest.builder().name("Existente").email("x@x.com")
                .phoneNumber("+34600000003").build();
        when(providerRepository.existsByName("Existente")).thenReturn(true);

        assertThatThrownBy(() -> service.create(req)).isInstanceOf(ProviderException.class);
    }

    @Test
    void create_duplicateEmail_throwsProviderException() {
        ProviderRequest req = ProviderRequest.builder().name("Nuevo2").email("dup@x.com")
                .phoneNumber("+34600000004").build();
        when(providerRepository.existsByName("Nuevo2")).thenReturn(false);
        when(providerRepository.existsByEmail("dup@x.com")).thenReturn(true);

        assertThatThrownBy(() -> service.create(req)).isInstanceOf(ProviderException.class);
    }

    @Test
    void activate_setsActiveTrueAndSaves() {
        Provider provider = buildProvider(1, false);
        when(providerRepository.findById(1)).thenReturn(Optional.of(provider));
        when(providerRepository.save(provider)).thenReturn(provider);

        ProviderResponse response = service.activate(1);

        assertThat(response.isActive()).isTrue();
    }

    @Test
    void deactivate_setsActiveFalseAndSaves() {
        Provider provider = buildProvider(1, true);
        when(providerRepository.findById(1)).thenReturn(Optional.of(provider));
        when(providerRepository.save(provider)).thenReturn(provider);

        ProviderResponse response = service.deactivate(1);

        assertThat(response.isActive()).isFalse();
    }
}

package com.ogc_prototype.ogc.mapper;

import com.ogc_prototype.ogc.dto.request.ProviderRequest;
import com.ogc_prototype.ogc.dto.response.ProviderResponse;
import com.ogc_prototype.ogc.model.Provider;

public class ProviderMapper {

    private ProviderMapper() {}

    public static ProviderResponse toResponse(Provider provider) {
        return ProviderResponse.builder().id(provider.getId()).name(provider.getName())
                .email(provider.getEmail()).phoneNumber(provider.getPhoneNumber())
                .website(provider.getWebsite()).notes(provider.getNotes())
                .active(provider.isActive()).build();
    }

    public static Provider toEntity(ProviderRequest request) {
        return Provider.builder().name(request.getName()).email(request.getEmail())
                .phoneNumber(request.getPhoneNumber()).website(request.getWebsite())
                .notes(request.getNotes()).active(request.isActive()).build();
    }
}

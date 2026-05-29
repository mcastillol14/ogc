package com.ogc_prototype.ogc.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    @NotBlank
    @Email
    @Size(max = 254)
    private String email;

    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    @Size(max = 200)
    private String website;

    @Size(max = 1000)
    private String notes;

    @Builder.Default
    private boolean active = true;
}


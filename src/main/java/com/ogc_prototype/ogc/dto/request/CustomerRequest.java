package com.ogc_prototype.ogc.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 254)
    private String email;

    @NotBlank
    @Size(max = 50)
    private String userName;

    @NotBlank
    @Size(min = 24, max = 72)
    private String password;

    @NotBlank
    @Pattern(regexp = "^(\\+34|0034)?[6-9]\\d{8}$",
            message = "Must be a valid Spanish phone number")
    private String phoneNumber;

    @NotBlank
    @Size(max = 200)
    private String address;

    @NotBlank
    @Size(max = 100)
    private String city;

    @NotNull
    @Min(1000)
    @Max(52999)
    private Integer zipCode;

    @NotBlank
    @Size(max = 100)
    private String country;

    @Builder.Default
    private boolean newsletterSubscribed = false;
}


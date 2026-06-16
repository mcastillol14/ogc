package com.ogc_prototype.ogc.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,72}$",
            message = "La contraseña debe tener mínimo 8 caracteres, al menos una mayúscula, una minúscula y un número")
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

    @NotBlank
    @Pattern(regexp = "^\\d{5}$", message = "El código postal debe tener exactamente 5 dígitos")
    private String zipCode;

    @NotBlank
    @Size(max = 100)
    private String country;

    private Boolean newsletterSubscribed;
}


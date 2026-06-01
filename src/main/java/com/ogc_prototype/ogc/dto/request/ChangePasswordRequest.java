package com.ogc_prototype.ogc.dto.request;

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
public class ChangePasswordRequest {

    @NotBlank
    private String currentPassword;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,72}$",
            message = "La contraseña debe tener mínimo 8 caracteres, al menos una mayúscula, una minúscula y un número")
    private String newPassword;

    @NotBlank
    @Size(min = 6, max = 6, message = "El código debe tener 6 dígitos")
    private String code;
}

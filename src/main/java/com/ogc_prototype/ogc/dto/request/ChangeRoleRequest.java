package com.ogc_prototype.ogc.dto.request;

import com.ogc_prototype.ogc.model.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeRoleRequest {

    @NotNull(message = "El rol no puede ser nulo")
    private Role role;
}

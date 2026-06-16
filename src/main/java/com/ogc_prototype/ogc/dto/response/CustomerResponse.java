package com.ogc_prototype.ogc.dto.response;

import com.ogc_prototype.ogc.model.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResponse {
    private Integer id;
    private String name;
    private String lastName;
    private String email;
    private String userName;
    private Role role;
    private String phoneNumber;
    private String address;
    private String city;
    private String zipCode;
    private String country;
    private boolean newsletterSubscribed;
}


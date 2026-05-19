package com.ogc_prototype.ogc.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProviderResponse {
    private Integer id;
    private String name;
    private String email;
    private String phoneNumber;
    private String website;
    private String notes;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


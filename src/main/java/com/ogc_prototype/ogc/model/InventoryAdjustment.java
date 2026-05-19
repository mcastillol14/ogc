package com.ogc_prototype.ogc.model;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inventory_adjustments")
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class InventoryAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    // los ajustes no se modifican, se crean nuevos para corregir
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}


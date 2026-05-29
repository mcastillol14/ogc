package com.ogc_prototype.ogc.model;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
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
@Table(name = "products")
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false, unique = true)
    private String skuCode;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Double price = 0.0;

    @Builder.Default
    @Column(nullable = false)
    private Double cbdPercentage = 0.0;

    @Builder.Default
    @Column(nullable = false)
    private Double thcPercentage = 0.0;

    @Builder.Default
    @Column(name = "oh_ten_percentage", nullable = false)
    private Double ohTenPercentage = 0.0;

    @Builder.Default
    @Column(nullable = false)
    private Double msPercentage = 0.0;

    @Builder.Default
    @Column(nullable = false)
    private Double nano10Percentage = 0.0;

    @Builder.Default
    @Column(nullable = false)
    private Double deltaHcPercentage = 0.0;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}


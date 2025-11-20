package com.cafeteriasoma.app.dto.producto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDto {
    private String id;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.cafeteriasoma.app.dto.producto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductoRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    
    private String description;
    
    @NotBlank(message = "La categoría es obligatoria")
    private String category;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;
    
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
    
    private String image;
}

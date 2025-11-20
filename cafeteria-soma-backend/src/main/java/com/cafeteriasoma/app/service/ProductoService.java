package com.cafeteriasoma.app.service;

import com.cafeteriasoma.app.dto.producto.CreateProductoRequest;
import com.cafeteriasoma.app.dto.producto.ProductoDto;
import com.cafeteriasoma.app.dto.producto.UpdateProductoRequest;
import com.cafeteriasoma.app.entity.Categoria;
import com.cafeteriasoma.app.entity.Producto;
import com.cafeteriasoma.app.repository.CategoriaRepository;
import com.cafeteriasoma.app.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<ProductoDto> getAllProductos() {
        return productoRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoDto getProductoById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return mapToDto(producto);
    }

    @Transactional
    public ProductoDto createProducto(CreateProductoRequest request) {
        Categoria categoria = categoriaRepository.findByNombre(request.getCategory())
                .orElseGet(() -> {
                    Categoria nuevaCategoria = new Categoria();
                    nuevaCategoria.setNombre(request.getCategory());
                    nuevaCategoria.setActivo(true);
                    return categoriaRepository.save(nuevaCategoria);
                });

        Producto producto = Producto.builder()
                .nombre(request.getName())
                .descripcion(request.getDescription())
                .precio(request.getPrice())
                .stock(request.getStock())
                .imagenUrl(request.getImage())
                .categoria(categoria)
                .activo(true)
                .build();

        producto = productoRepository.save(producto);
        return mapToDto(producto);
    }

    @Transactional
    public ProductoDto updateProducto(Long id, UpdateProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (request.getName() != null) producto.setNombre(request.getName());
        if (request.getDescription() != null) producto.setDescripcion(request.getDescription());
        if (request.getPrice() != null) producto.setPrecio(request.getPrice());
        if (request.getStock() != null) producto.setStock(request.getStock());
        if (request.getImage() != null) producto.setImagenUrl(request.getImage());
        
        if (request.getCategory() != null) {
            Categoria categoria = categoriaRepository.findByNombre(request.getCategory())
                    .orElseGet(() -> {
                        Categoria nuevaCategoria = new Categoria();
                        nuevaCategoria.setNombre(request.getCategory());
                        nuevaCategoria.setActivo(true);
                        return categoriaRepository.save(nuevaCategoria);
                    });
            producto.setCategoria(categoria);
        }

        producto = productoRepository.save(producto);
        return mapToDto(producto);
    }

    @Transactional
    public void deleteProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    private ProductoDto mapToDto(Producto producto) {
        return ProductoDto.builder()
                .id(producto.getIdProducto().toString())
                .name(producto.getNombre())
                .description(producto.getDescripcion())
                .category(producto.getCategoria().getNombre())
                .price(producto.getPrecio())
                .stock(producto.getStock())
                .image(producto.getImagenUrl())
                .isActive(producto.getActivo())
                .createdAt(producto.getFechaCreacion())
                .updatedAt(producto.getFechaActualizacion())
                .build();
    }
}

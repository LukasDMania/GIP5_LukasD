package be.ucll.application.mapper.product;

import be.ucll.application.dto.product.ProductRequestDto;
import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.application.dto.product.ProductUpdateRequestDto;
import be.ucll.domain.model.Product;

import java.time.LocalDateTime;

public class ProductMapper {
    public static Product toProductEntity(ProductRequestDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setStock(dto.getInitialStock());
        product.setDescription(dto.getDescription());
        product.setCreatedAt(LocalDateTime.now());
        return product;
    }

    public static Product toProductEntity(ProductUpdateRequestDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setStock(dto.getStock());
        product.setDescription(dto.getDescription());
        product.setCreatedAt(LocalDateTime.now());
        return product;
    }

    public static ProductResponseDto toResponseDto(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getStock(),
                product.getDescription(),
                product.getCreatedAt()
        );
    }
}


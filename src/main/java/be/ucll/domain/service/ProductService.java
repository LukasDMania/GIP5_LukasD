package be.ucll.domain.service;

import be.ucll.application.dto.product.ProductRequestDto;
import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.application.dto.product.ProductUpdateRequestDto;
import be.ucll.application.dto.stockadjustment.StockAdjustmentRequestDto;
import be.ucll.application.dto.stockadjustment.StockAdjustmentResponseDto;

import java.util.List;

public interface ProductService {
    List<ProductResponseDto> findAll();
    ProductResponseDto findById(Long id);
    ProductResponseDto createProduct(ProductRequestDto request, String performedByUsername);
    ProductResponseDto updateProduct(ProductUpdateRequestDto request, String performedByUsername);
    void deleteProduct(Long id);
    List<String> autocompleteProductNames(String prefix);
    StockAdjustmentResponseDto adjustStock(StockAdjustmentRequestDto request);
}

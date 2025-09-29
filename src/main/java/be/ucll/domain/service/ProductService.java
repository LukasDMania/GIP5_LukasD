package be.ucll.domain.service;

import be.ucll.application.dto.SearchCriteriaDto;
import be.ucll.application.dto.product.ProductRequestDto;
import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.application.dto.product.ProductUpdateRequestDto;
import be.ucll.application.dto.stockadjustment.StockAdjustmentRequestDto;
import be.ucll.application.dto.stockadjustment.StockAdjustmentResponseDto;
import be.ucll.domain.model.Product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ProductService {
    List<ProductResponseDto> findAll();
    ProductResponseDto findById(Long id);
    Product getProductById(Long id);
    ProductResponseDto createProduct(ProductRequestDto request, String performedByUsername);
    ProductResponseDto updateProduct(ProductUpdateRequestDto request, String performedByUsername);
    void deleteProduct(Long id);
    List<String> autocompleteProductNames(String prefix);
    List<ProductResponseDto> searchProductsByCriteria(SearchCriteriaDto searchCriteriaDto);
    StockAdjustmentResponseDto adjustStock(StockAdjustmentRequestDto request);
    List<ProductResponseDto> searchProductsByCriteriaAndPublish(SearchCriteriaDto searchCriteriaDto);
    int totalProducts();
    int totalStock();
    ProductResponseDto mostAdjustedProduct();
    Map<LocalDateTime, Long> getProductCreationCountOverTime();
    List<Product> getTopProductsByStock(int limit);
    double getAverageStockLevel();
    Map<Product, Long> getAdjustmentCountsPerProduct();
}

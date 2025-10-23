package be.ucll.domain.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import be.ucll.domain.model.Product;
import be.ucll.application.dto.SearchCriteriaDto;
import be.ucll.application.dto.product.ProductRequestDto;
import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.application.dto.product.ProductUpdateRequestDto;
import be.ucll.application.dto.stockadjustment.StockAdjustmentRequestDto;
import be.ucll.application.dto.stockadjustment.StockAdjustmentResponseDto;

public interface ProductService {

    List<ProductResponseDto> findAll();
    ProductResponseDto findById(Long id);
    Product getProductById(Long id);

    ProductResponseDto createProduct(ProductRequestDto request, String performedByUsername);
    ProductResponseDto updateProduct(ProductUpdateRequestDto request, String performedByUsername);
    void deleteProduct(Long id);

    List<String> autocompleteProductNames(String prefix);
    List<ProductResponseDto> searchProductsByCriteria(SearchCriteriaDto searchCriteriaDto);
    List<ProductResponseDto> searchProductsByCriteriaAndPublish(SearchCriteriaDto searchCriteriaDto);

    StockAdjustmentResponseDto adjustStock(StockAdjustmentRequestDto request);

    int totalProducts();
    int totalStock();
    double getAverageStockLevel();

    ProductResponseDto mostAdjustedProduct();
    Map<LocalDateTime, Long> getProductCreationCountOverTime();
    Map<Product, Long> getAdjustmentCountsPerProduct();

    List<Product> getTopProductsByStock(int limit);
}

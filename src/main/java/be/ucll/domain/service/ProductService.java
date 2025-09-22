package be.ucll.domain.service;

import be.ucll.application.dto.product.ProductRequest;
import be.ucll.application.dto.product.ProductResponse;
import be.ucll.application.dto.product.ProductUpdateRequest;
import be.ucll.application.dto.stockadjustment.StockAdjustmentRequest;
import be.ucll.application.dto.stockadjustment.StockAdjustmentResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> findAll();
    ProductResponse findById(Long id);
    ProductResponse createProduct(ProductRequest request, String performedByUsername);
    ProductResponse updateProduct(ProductUpdateRequest request, String performedByUsername);
    void deleteProduct(Long id, String performedByUsername);
    StockAdjustmentResponse adjustStock(StockAdjustmentRequest request);
}

package be.ucll.domain.service.impl;

import be.ucll.application.dto.product.ProductResponse;
import be.ucll.domain.model.Product;
import be.ucll.domain.repository.ProductRepository;
import be.ucll.domain.service.StockAdjustmentService;
import be.ucll.exception.product.ProductNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final StockAdjustmentService stockAdjustmentService;

    private static final int MAX_ABSOLUTE_DELTA = 100000000;

    public ProductServiceImpl(ProductRepository productRepository,
                              StockAdjustmentService stockAdjustmentService) {
        this.productRepository = productRepository;
        this.stockAdjustmentService = stockAdjustmentService;
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_USER')")
    @Transactional(rollbackOn = Exception.class)
    public List<ProductResponse> findAll() {
        LOG.debug("PRODUCTS findAll()");
        return productRepository.findAll().stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_USER')")
    public ProductResponse findById(Long id) {
        LOG.debug("PRODUCTS findById({})", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductMapper.toResponse(product);
    }
}

package be.ucll.domain.service.impl;

import be.ucll.application.dto.product.ProductRequestDto;
import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.application.dto.product.ProductUpdateRequestDto;
import be.ucll.application.mapper.product.ProductMapper;
import be.ucll.domain.model.Product;
import be.ucll.domain.model.User;
import be.ucll.domain.repository.ProductRepository;
import be.ucll.domain.service.StockAdjustmentService;
import be.ucll.domain.service.UserService;
import be.ucll.exception.DataIntegrityException;
import be.ucll.exception.product.ProductAlreadyExistsException;
import be.ucll.exception.product.ProductNotFoundException;
import be.ucll.exception.product.ProductUpdateException;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final UserServiceImpl userService;
    private final StockAdjustmentService stockAdjustmentService;

    private static final int MAX_ABSOLUTE_DELTA = 100000000;

    public ProductServiceImpl(ProductRepository productRepository,
                              StockAdjustmentService stockAdjustmentService,
                              UserServiceImpl userService) {
        this.productRepository = productRepository;
        this.stockAdjustmentService = stockAdjustmentService;
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_USER')")
    @Transactional()
    public List<ProductResponseDto> findAll() {
        LOG.debug("PRODUCTS findAll()");
        return productRepository.findAll().stream()
                .map(ProductMapper::toResponseDto)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_USER')")
    public ProductResponseDto findById(Long id) {
        LOG.debug("PRODUCTS findById({})", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductMapper.toResponseDto(product);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Transactional()
    public ProductResponseDto createProduct(ProductRequestDto productRequest, String performedByUsername) {
        LOG.info("PRODUCTS createProduct({}, {})", performedByUsername, productRequest.getName());

        if (productRepository.existsByName(productRequest.getName().trim())) {
            throw new ProductAlreadyExistsException(productRequest.getName());
        }

        Product  product = ProductMapper.toProductEntity(productRequest);

        try {
            Product savedProduct =  productRepository.save(product);
            return ProductMapper.toResponseDto(savedProduct);
        } catch (DataIntegrityException ex) {
            LOG.error("Data integrity violation creating product {}", productRequest.getName(), ex);
            throw new DataIntegrityException("Unable to create product", ex);
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Transactional()
    public ProductResponseDto updateProduct(ProductUpdateRequestDto productUpdateRequestDto, String performedByUsername) {
        LOG.info("Request to update product id={}", productUpdateRequestDto.getId());

        Product existingProduct = productRepository.findById(productUpdateRequestDto.getId())
                .orElseThrow(() -> new ProductNotFoundException(productUpdateRequestDto.getId()));
        
        try {
            int oldStock = existingProduct.getStock();
            int newStock = productUpdateRequestDto.getStock();
            int delta =  newStock - oldStock;

            existingProduct.setName(productUpdateRequestDto.getName());
            existingProduct.setDescription(productUpdateRequestDto.getDescription());
            existingProduct.setStock(newStock);

            Product savedProduct = productRepository.save(existingProduct);
            LOG.info("Product id={} successfully updated", savedProduct.getId());

            if (delta != 0) {
                if (Math.abs(delta) > MAX_ABSOLUTE_DELTA) {
                    throw new ProductUpdateException("Stock change too large");
                }
                User currentUser = userService.getDomainUserByUsername(performedByUsername);
                stockAdjustmentService.recordAdjustment(savedProduct, currentUser, delta);
            }

             return ProductMapper.toResponseDto(savedProduct);

        } catch (Exception e) {
            LOG.error("Failed to update product id={}: {}", productUpdateRequestDto.getId(), e.getMessage(), e);
            throw new ProductUpdateException(productUpdateRequestDto.getId());
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

}

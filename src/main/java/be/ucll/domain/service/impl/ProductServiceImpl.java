package be.ucll.domain.service.impl;

import be.ucll.application.dto.SearchCriteriaDto;
import be.ucll.application.dto.product.ProductRequestDto;
import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.application.dto.product.ProductUpdateRequestDto;
import be.ucll.application.dto.stockadjustment.StockAdjustmentRequestDto;
import be.ucll.application.dto.stockadjustment.StockAdjustmentResponseDto;
import be.ucll.application.events.SearchCompletedEvent;
import be.ucll.application.mapper.product.ProductMapper;
import be.ucll.domain.model.Product;
import be.ucll.domain.model.StockAdjustment;
import be.ucll.domain.model.User;
import be.ucll.domain.repository.ProductRepository;
import be.ucll.domain.repository.StockAdjustmentRepository;
import be.ucll.domain.service.ProductService;
import be.ucll.domain.service.StockAdjustmentService;
import be.ucll.exception.DataIntegrityException;
import be.ucll.exception.product.ProductAlreadyExistsException;
import be.ucll.exception.product.ProductNotFoundException;
import be.ucll.exception.product.ProductUpdateException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ApplicationEventPublisher springEventPublisher;

    private final ProductRepository productRepository;
    private final UserServiceImpl userService;
    private final StockAdjustmentService stockAdjustmentService;
    private final StockAdjustmentRepository stockAdjustmentRepository;

    private static final int MAX_ABSOLUTE_DELTA = 100000000;

    public ProductServiceImpl(ProductRepository productRepository,
                              StockAdjustmentService stockAdjustmentService,
                              UserServiceImpl userService, StockAdjustmentRepository stockAdjustmentRepository) {
        this.productRepository = productRepository;
        this.stockAdjustmentService = stockAdjustmentService;
        this.userService = userService;
        this.stockAdjustmentRepository = stockAdjustmentRepository;
    }

    @PreAuthorize("hasAnyRole('MANAGER','USER')")
    @Transactional()
    public List<ProductResponseDto> findAll() {
        LOG.debug("PRODUCTS findAll()");
        return productRepository.findAll().stream()
                .map(ProductMapper::toResponseDto)
                .toList();
    }

    @PreAuthorize("hasAnyRole('MANAGER','USER')")
    @Override
    public List<ProductResponseDto> searchProductsByCriteria(SearchCriteriaDto searchCriteriaDto) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> productRoot = query.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        if (searchCriteriaDto.getMinStock() > 0) {
            predicates.add(cb.ge(productRoot.get("stock"), searchCriteriaDto.getMinStock()));
        }

        if (searchCriteriaDto.getMaxStock() > 0) {
            predicates.add(cb.le(productRoot.get("stock"), searchCriteriaDto.getMaxStock()));
        }

        if (searchCriteriaDto.getProductName() != null && !searchCriteriaDto.getProductName().isBlank()) {
            predicates.add(
                    cb.like(
                            cb.lower(productRoot.get("name")),
                            "%" + searchCriteriaDto.getProductName().toLowerCase() + "%"
                    )
            );
        }

        if (searchCriteriaDto.getCreatedAfter() != null) {
            predicates.add(cb.greaterThanOrEqualTo(productRoot.get("createdAt"), searchCriteriaDto.getCreatedAfter()));
        }

        query.select(productRoot).where(predicates.toArray(new Predicate[0]));

        List<Product> resultList = em.createQuery(query).getResultList();

        return resultList.stream()
                .map(ProductMapper::toResponseDto)
                .toList();
    }

    @PreAuthorize("hasAnyRole('MANAGER','USER')")
    @Override
    public List<ProductResponseDto> searchProductsByCriteriaAndPublish(SearchCriteriaDto searchCriteriaDto) {
        List<ProductResponseDto> dtos = searchProductsByCriteria(searchCriteriaDto);

        springEventPublisher.publishEvent(new SearchCompletedEvent(dtos,  searchCriteriaDto));
        return dtos;
    }

    @Override
    public int totalProducts() {
        return (int)productRepository.count();
    }

    @Override
    public int totalStock() {
        return productRepository.sumStock();
    }

    @Override
    public ProductResponseDto mostAdjustedProduct() {
        Map<Product, Long> counts = stockAdjustmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(StockAdjustment::getProduct, Collectors.counting()));

        Map.Entry<Product, Long> max = counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        return max != null ? ProductMapper.toResponseDto(max.getKey()) : null;
    }

    @PreAuthorize("hasAnyRole('MANAGER','USER')")
    public ProductResponseDto findById(Long id) {
        LOG.debug("PRODUCTS findById({})", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductMapper.toResponseDto(product);
    }

    public Product getProductById(Long id) {
        LOG.debug("PRODUCTS findById({})", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return product;
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
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

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
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

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    public List<String> autocompleteProductNames(String prefix) {
        List<Product> productList = productRepository.findNameByNameStartingWithIgnoreCase(prefix);
        List<String> produktNameList = new ArrayList<>();
        for (Product product : productList) {
            produktNameList.add(product.getName());
        }
        return produktNameList;
    }

    @Override
    public StockAdjustmentResponseDto adjustStock(StockAdjustmentRequestDto productUpdateRequestDto) {
        Product existingProduct = productRepository.findById(productUpdateRequestDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(productUpdateRequestDto.getProductId()));

        int newStock = existingProduct.getStock() + productUpdateRequestDto.getDelta();
        if (newStock < 0) {
            throw new ProductUpdateException("Stock cannot go negative");
        }
        existingProduct.setStock(newStock);
        Product savedProduct = productRepository.save(existingProduct);

        User currentUser = userService.getDomainUserByUsername(productUpdateRequestDto.getPerformedByUsername());
        StockAdjustment adjustment =
                stockAdjustmentService.recordAdjustment(savedProduct, currentUser, productUpdateRequestDto.getDelta());

        StockAdjustmentResponseDto responseDto = new StockAdjustmentResponseDto();
        responseDto.setAdjustmentId(adjustment.getId());
        responseDto.setProductId(savedProduct.getId());
        responseDto.setDelta(adjustment.getDelta());
        responseDto.setPerformedByUsername(adjustment.getAdjustedBy().getUsername());
        responseDto.setTimestamp(adjustment.getTimestamp());

        return responseDto;
    }

    public Map<LocalDateTime, Long> getProductCreationCountOverTime() {
        return productRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        product -> product.getCreatedAt().withHour(0).withMinute(0).withSecond(0).withNano(0),
                        Collectors.counting()
                ));
    }

    public List<Product> getTopProductsByStock(int limit) {
        return productRepository.findAll().stream()
                .sorted((p1, p2) -> Integer.compare(p2.getStock(), p1.getStock()))
                .limit(limit)
                .toList();
    }

    public double getAverageStockLevel() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) return 0.0;
        return products.stream().mapToInt(Product::getStock).average().orElse(0);
    }

    public Map<Product, Long> getAdjustmentCountsPerProduct() {
        return stockAdjustmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(StockAdjustment::getProduct, Collectors.counting()));
    }

}

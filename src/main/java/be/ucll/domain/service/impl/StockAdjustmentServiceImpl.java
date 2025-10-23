package be.ucll.domain.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import be.ucll.domain.model.Product;
import be.ucll.domain.model.StockAdjustment;
import be.ucll.domain.model.User;
import be.ucll.domain.repository.ProductRepository;
import be.ucll.domain.repository.StockAdjustmentRepository;
import be.ucll.domain.service.StockAdjustmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StockAdjustmentServiceImpl implements StockAdjustmentService {
    private static final Logger LOG = LoggerFactory.getLogger(StockAdjustmentServiceImpl.class);

    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final ProductRepository productRepository;

    public StockAdjustmentServiceImpl(StockAdjustmentRepository stockAdjustmentRepository, ProductRepository productRepository) {
        this.stockAdjustmentRepository = stockAdjustmentRepository;
        this.productRepository = productRepository;
    }

    @Override
    public StockAdjustment recordAdjustment(Product product, User user, int delta) {
        StockAdjustment stockAdjustment = new StockAdjustment();
        stockAdjustment.setProduct(product);
        stockAdjustment.setAdjustedBy(user);
        stockAdjustment.setDelta(delta);
        stockAdjustment.setTimestamp(LocalDateTime.now());
        stockAdjustment.setStockAfter(product.getStock());
        StockAdjustment savedStockAdjustment = stockAdjustmentRepository.save(stockAdjustment);

        LOG.debug("Recorded stock adjustment id={} productId={} delta={} by={}",
                savedStockAdjustment.getId(), product.getId(), delta, user.getUsername());

        return savedStockAdjustment;
    }

    @Override
    public List<StockAdjustment> findByProduct(Product product) {
        return stockAdjustmentRepository.findByProductOrderByTimestampDesc(product);
    }

    @Override
    public int getTotalStockAdjustments() {
        return (int) stockAdjustmentRepository.count();
    }

    @Override
    public Map<Product, Integer> getTotalDeltaPerProduct() {
        return stockAdjustmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        StockAdjustment::getProduct,
                        Collectors.summingInt(StockAdjustment::getDelta)
                ));
    }

    @Override
    public Map<Product, Long> getAdjustmentCountsPerProduct() {
        return stockAdjustmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        StockAdjustment::getProduct,
                        Collectors.counting()
                ));
    }

    @Override
    public User getMostActiveUserByAdjustments() {
        return stockAdjustmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(StockAdjustment::getAdjustedBy, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    @Override
    public Map<User, Integer> getTotalDeltaPerUser() {
        return stockAdjustmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        StockAdjustment::getAdjustedBy,
                        Collectors.summingInt(StockAdjustment::getDelta)
                ));
    }

    @Override
    public Map<User, Long> getAdjustmentCountsPerUser() {
        return stockAdjustmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        StockAdjustment::getAdjustedBy,
                        Collectors.counting()
                ));
    }

    @Override
    public int getTotalActiveUsers(){
        return (int)stockAdjustmentRepository.findAll().stream()
                .map(stockAdjustment -> stockAdjustment.getAdjustedBy().getId())
                .distinct()
                .count();
    }

    @Override
    public float getAverageAdjustments(){
        List<StockAdjustment> adjustments = stockAdjustmentRepository.findAll();
        if (adjustments.isEmpty()) return 0;
        float totalAdjustments = adjustments.size();
        float uniqueUsers = adjustments.stream()
                .map(adj -> adj.getAdjustedBy().getId())
                .distinct()
                .count();
        if (uniqueUsers == 0) return 0;
        return totalAdjustments / uniqueUsers;
    }

    @Override
    public Map<String, Long> getAdjustmentsPerUser() {
        return stockAdjustmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        adj -> adj.getAdjustedBy().getUsername(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }

    @Override
    public Map<String, Double> getAverageAdjustmentSizePerUser() {
        return stockAdjustmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        adj -> adj.getAdjustedBy().getUsername(),
                        LinkedHashMap::new,
                        Collectors.averagingInt(StockAdjustment::getDelta)
                ));
    }

    @Override
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findAll().stream()
                .filter(p -> p.getStock() < threshold)
                .sorted(Comparator.comparingInt(Product::getStock))
                .toList();
    }

    @Override
    public List<Product> getOverstockedProducts(int threshold) {
        return productRepository.findAll().stream()
                .filter(p -> p.getStock() > threshold)
                .sorted(Comparator.comparingInt(Product::getStock).reversed())
                .toList();
    }

    @Override
    public List<Product> getTopProductsByStock(int limit) {
        return productRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Product::getStock).reversed())
                .limit(limit)
                .toList();
    }

    @Override
    public List<Product> getTopProductsByPositiveDelta(int limit) {
        return getTotalDeltaPerProduct().entrySet().stream()
                .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
                .filter(e -> e.getValue() > 0)
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public List<Product> getTopProductsByNegativeDelta(int limit) {
        return getTotalDeltaPerProduct().entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .filter(e -> e.getValue() < 0)
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public List<StockAdjustment> getRecentAdjustments(int limit) {
        return stockAdjustmentRepository.findAll().stream()
                .sorted(Comparator.comparing(StockAdjustment::getTimestamp).reversed())
                .limit(limit)
                .toList();
    }

    @Override
    public Map<LocalDateTime, Long> getAdjustmentsOverTime() {
        return stockAdjustmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        adj -> adj.getTimestamp().withHour(0).withMinute(0).withSecond(0).withNano(0),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }

    @Override
    public Map<LocalDateTime, Integer> getStockOverTime() {
        Map<LocalDateTime, Integer> result = new LinkedHashMap<>();
        stockAdjustmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        adj -> adj.getTimestamp().withHour(0).withMinute(0).withSecond(0).withNano(0),
                        TreeMap::new,
                        Collectors.summingInt(StockAdjustment::getDelta)
                ))
                .forEach(result::put);
        return result;
    }

    @Override
    public Map<LocalDateTime, Long> getStockoutsOverTime() {
        return stockAdjustmentRepository.findAll().stream()
                .filter(adj -> adj.getStockAfter() == 0)
                .collect(Collectors.groupingBy(
                        adj -> adj.getTimestamp().withHour(0).withMinute(0).withSecond(0).withNano(0),
                        TreeMap::new,
                        Collectors.counting()
                ));
    }
}
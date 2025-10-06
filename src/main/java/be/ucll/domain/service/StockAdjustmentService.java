package be.ucll.domain.service;

import be.ucll.domain.model.Product;
import be.ucll.domain.model.StockAdjustment;
import be.ucll.domain.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StockAdjustmentService {
    StockAdjustment recordAdjustment(Product product, User user, int delta);
    List<StockAdjustment> findByProduct(Product product);
    int getTotalStockAdjustments();
    User getMostActiveUserByAdjustments();

    List<Product> getLowStockProducts(int threshold);
    List<Product> getOverstockedProducts(int threshold);
    Map<Product, Integer> getTotalDeltaPerProduct();
    Map<Product, Long> getAdjustmentCountsPerProduct();

    Map<LocalDateTime, Integer> getStockOverTime();
    Map<LocalDateTime, Long> getStockoutsOverTime();
    Map<LocalDateTime, Long> getAdjustmentsOverTime();

    Map<User, Integer> getTotalDeltaPerUser();
    Map<User, Long> getAdjustmentCountsPerUser();

    List<Product> getTopProductsByStock(int limit);
    List<Product> getTopProductsByPositiveDelta(int limit);
    List<Product> getTopProductsByNegativeDelta(int limit);
    List<StockAdjustment> getRecentAdjustments(int limit);

    int getTotalActiveUsers();
    float getAverageAdjustments();
    Map<String, Long> getAdjustmentsPerUser();
    Map<String, Double> getAverageAdjustmentSizePerUser();
}

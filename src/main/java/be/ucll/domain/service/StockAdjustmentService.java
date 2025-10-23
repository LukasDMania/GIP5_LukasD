package be.ucll.domain.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import be.ucll.domain.model.Product;
import be.ucll.domain.model.StockAdjustment;
import be.ucll.domain.model.User;

public interface StockAdjustmentService {

    StockAdjustment recordAdjustment(Product product, User user, int delta);
    List<StockAdjustment> findByProduct(Product product);
    List<StockAdjustment> getRecentAdjustments(int limit);

    List<Product> getLowStockProducts(int threshold);
    List<Product> getOverstockedProducts(int threshold);

    List<Product> getTopProductsByStock(int limit);
    List<Product> getTopProductsByPositiveDelta(int limit);
    List<Product> getTopProductsByNegativeDelta(int limit);

    Map<Product, Integer> getTotalDeltaPerProduct();
    Map<Product, Long> getAdjustmentCountsPerProduct();

    Map<User, Integer> getTotalDeltaPerUser();
    Map<User, Long> getAdjustmentCountsPerUser();

    Map<String, Long> getAdjustmentsPerUser();
    Map<String, Double> getAverageAdjustmentSizePerUser();

    Map<LocalDateTime, Integer> getStockOverTime();
    Map<LocalDateTime, Long> getStockoutsOverTime();
    Map<LocalDateTime, Long> getAdjustmentsOverTime();

    int getTotalStockAdjustments();
    int getTotalActiveUsers();
    float getAverageAdjustments();

    User getMostActiveUserByAdjustments();
}

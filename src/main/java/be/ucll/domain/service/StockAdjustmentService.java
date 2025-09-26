package be.ucll.domain.service;

import be.ucll.domain.model.Product;
import be.ucll.domain.model.StockAdjustment;
import be.ucll.domain.model.User;

import java.util.List;

public interface StockAdjustmentService {
    StockAdjustment recordAdjustment(Product product, User user, int delta);
    List<StockAdjustment> findByProduct(Product product);
}

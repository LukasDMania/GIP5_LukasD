package be.ucll.domain.service;

import be.ucll.domain.model.Product;
import be.ucll.domain.model.StockAdjustment;
import be.ucll.domain.model.User;

public interface StockAdjustmentService {
    public StockAdjustment recordAdjustment(Product product, User user, int delta);
}

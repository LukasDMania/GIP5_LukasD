package be.ucll.domain.service.impl;

import be.ucll.domain.model.Product;
import be.ucll.domain.model.StockAdjustment;
import be.ucll.domain.model.User;
import be.ucll.domain.repository.StockAdjustmentRepository;
import be.ucll.domain.service.StockAdjustmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StockAdjustmentServiceImpl implements StockAdjustmentService {

    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final Logger LOG = LoggerFactory.getLogger(StockAdjustmentServiceImpl.class);

    public StockAdjustmentServiceImpl(StockAdjustmentRepository stockAdjustmentRepository) {
        this.stockAdjustmentRepository = stockAdjustmentRepository;
    }

    public StockAdjustment recordAdjustment(Product product, User user, int delta) {
        StockAdjustment stockAdjustment = new StockAdjustment();
        stockAdjustment.setProduct(product);
        stockAdjustment.setAdjustedBy(user);
        stockAdjustment.setDelta(delta);
        stockAdjustment.setTimestamp(LocalDateTime.now());
        StockAdjustment savedStockAdjustment = stockAdjustmentRepository.save(stockAdjustment);

        LOG.debug("Recorded stock adjustment id={} productId={} delta={} by={}",
                savedStockAdjustment.getId(), product.getId(), delta, user.getUsername());

        return savedStockAdjustment;
    }
}

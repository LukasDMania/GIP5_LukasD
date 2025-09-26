package be.ucll.domain.repository;

import be.ucll.domain.model.Product;
import be.ucll.domain.model.StockAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {
    List<StockAdjustment> findByProductOrderByTimestampDesc(Product product);
}

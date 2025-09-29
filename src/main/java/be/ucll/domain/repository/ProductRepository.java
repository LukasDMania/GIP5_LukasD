package be.ucll.domain.repository;

import be.ucll.domain.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
    Optional<Product> findByName(String name);
    List<Product> findNameByNameStartingWithIgnoreCase(String prefix);
    @Query("SELECT COALESCE(SUM(p.stock), 0) FROM Product p")
    int sumStock();
}

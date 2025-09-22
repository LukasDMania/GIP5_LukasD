package be.ucll.application.dto;

import java.time.LocalDateTime;

public class SearchCriteriaDto {
    private int minStock;
    private int maxStock;
    private String productName;
    private LocalDateTime createdAfter;

    public int getMinStock() {
        return minStock;
    }
    public void setMinStock(int minStock) {
        this.minStock = minStock;
    }

    public int getMaxStock() {
        return maxStock;
    }
    public void setMaxStock(int maxStock) {
        this.maxStock = maxStock;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public LocalDateTime getCreatedAfter() {
        return createdAfter;
    }
    public void setCreatedAfter(LocalDateTime createdAfter) {
        this.createdAfter = createdAfter;
    }
}

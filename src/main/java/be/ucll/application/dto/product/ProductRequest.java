package be.ucll.application.dto.product;

import jakarta.validation.constraints.Min;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

public class ProductRequest {
    @NotBlank
    private String name;

    @Min(0)
    private int initialStock;

    private String description;

    public ProductRequest(String name, int initialStock, String description) {
        this.name = name;
        this.initialStock = initialStock;
        this.description = description;
    }
    public ProductRequest(String name, int initialStock) {
        this.name = name;
        this.initialStock = initialStock;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getInitialStock() {
        return initialStock;
    }
    public void setInitialStock(int initialStock) {
        this.initialStock = initialStock;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}

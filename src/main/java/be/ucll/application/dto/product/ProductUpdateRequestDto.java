package be.ucll.application.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

public class ProductUpdateRequestDto {
    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @Min(0)
    private int stock;

    private String description;

    public ProductUpdateRequestDto(Long id, String name, Integer stock, String description) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.description = description;
    }
    public ProductUpdateRequestDto(String name, Integer stock) {
        this.name = name;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}

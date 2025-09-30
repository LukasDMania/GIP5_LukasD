package be.ucll.application.dto.product;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProductResponseDto {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Long id;
    private String name;
    private int stock;
    private String description;
    private LocalDateTime createdAt;

    public ProductResponseDto(Long id, String name, int stock, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.description = description;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    @Override
    public String toString() {
        return "ProductResponseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", stock=" + stock +
                ", description='" + description + '\'' +
                ", createdAt=" + (createdAt != null ? FORMATTER.format(createdAt) : "N/A") +
                '}';
    }

    public String formattedDateString(){
        return createdAt != null ? FORMATTER.format(createdAt) : "N/A";
    }
}

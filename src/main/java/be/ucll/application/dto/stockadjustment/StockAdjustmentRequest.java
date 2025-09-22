package be.ucll.application.dto.stockadjustment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StockAdjustmentRequest {
    @NotNull
    private Long productId;

    private int delta;

    @NotBlank
    private String performedByUsername;

    public StockAdjustmentRequest(Long productId, int delta, String performedByUsername) {
        this.productId = productId;
        this.delta = delta;
        this.performedByUsername = performedByUsername;
    }

    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getDelta() {
        return delta;
    }
    public void setDelta(int delta) {
        this.delta = delta;
    }

    public String getPerformedByUsername() {
        return performedByUsername;
    }
    public void setPerformedByUsername(String performedByUsername) {
        this.performedByUsername = performedByUsername;
    }
}

package be.ucll.application.dto.stockadjustment;

import java.time.LocalDateTime;

public class StockAdjustmentResponseDto {
    private Long adjustmentId;
    private Long productId;
    private int delta;
    private String performedByUsername;
    private LocalDateTime timestamp;

    public Long getAdjustmentId() {
        return adjustmentId;
    }
    public void setAdjustmentId(Long adjustmentId) {
        this.adjustmentId = adjustmentId;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

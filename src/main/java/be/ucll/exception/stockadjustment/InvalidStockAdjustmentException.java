package be.ucll.exception.stockadjustment;

public class InvalidStockAdjustmentException extends RuntimeException {
    public InvalidStockAdjustmentException(String message) {
        super(message);
    }
}

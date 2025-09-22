package be.ucll.exception.product;

public class ProductUpdateException extends RuntimeException {
    public ProductUpdateException(Long id) { super("Unable to update product: " + id); }
    public ProductUpdateException(String message) { super(message); }
}

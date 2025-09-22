package be.ucll.exception.product;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) { super("Product not found: " + id); }
}
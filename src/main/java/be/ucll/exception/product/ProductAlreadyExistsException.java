package be.ucll.exception.product;

public class ProductAlreadyExistsException extends RuntimeException {
  public ProductAlreadyExistsException(String name) { super("Product already exists: " + name); }
}

package be.ucll.ui.component;

import be.ucll.domain.model.Product;
import com.vaadin.flow.component.grid.Grid;

import java.util.Collections;

public class ProductGrid extends Grid<Product> {

    public ProductGrid() {
        removeAllColumns();

        addColumn(Product::getId).setHeader("Product Id");
        addColumn(Product::getName).setHeader("Name");
        addColumn(Product::getDescription).setHeader("Description");
        addColumn(Product::getStock).setHeader("Amount In Stock");
        addColumn(Product::getCreatedAt).setHeader("Created At");

        setWidthFull();
        setItems(Collections.emptyList());
    }
}

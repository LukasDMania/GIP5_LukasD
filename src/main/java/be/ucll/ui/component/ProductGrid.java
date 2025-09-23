package be.ucll.ui.component;

import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.domain.model.Product;
import com.vaadin.flow.component.grid.Grid;

import java.util.Collections;

public class ProductGrid extends Grid<ProductResponseDto> {

    public ProductGrid() {
        removeAllColumns();

        addColumn(ProductResponseDto::getId).setHeader("Product Id");
        addColumn(ProductResponseDto::getName).setHeader("Name");
        addColumn(ProductResponseDto::getDescription).setHeader("Description");
        addColumn(ProductResponseDto::getStock).setHeader("Amount In Stock");
        addColumn(ProductResponseDto::getCreatedAt).setHeader("Created At");

        setWidthFull();
        setItems(Collections.emptyList());
    }
}

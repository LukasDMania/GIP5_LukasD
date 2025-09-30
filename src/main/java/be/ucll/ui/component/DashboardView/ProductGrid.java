package be.ucll.ui.component.DashboardView;

import be.ucll.application.dto.product.ProductResponseDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;

import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class ProductGrid extends Grid<ProductResponseDto> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ProductGrid() {
        removeAllColumns();

        addColumn(ProductResponseDto::getId).setHeader("Product Id");
        addColumn(ProductResponseDto::getName).setHeader("Name");
        addColumn(ProductResponseDto::getDescription).setHeader("Description");
        addColumn(ProductResponseDto::getStock).setHeader("Amount In Stock");
        addColumn(product -> product.getCreatedAt() != null
                ? product.getCreatedAt().format(FORMATTER)
                : "")
                .setHeader("Created At");


        addComponentColumn(productResponseDto -> {
            Button detailButton = new Button("Details", event -> {
                getUI().ifPresent(ui -> ui.navigate("product/" + productResponseDto.getId()));
            });
            return detailButton;
        }).setHeader("Details");

        setWidthFull();
        setItems(Collections.emptyList());
    }
}

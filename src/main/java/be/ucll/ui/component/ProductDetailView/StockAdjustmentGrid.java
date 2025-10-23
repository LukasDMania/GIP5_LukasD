package be.ucll.ui.component.ProductDetailView;

import be.ucll.domain.model.StockAdjustment;
import com.vaadin.flow.component.grid.Grid;

import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class StockAdjustmentGrid extends Grid<StockAdjustment> {

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public StockAdjustmentGrid() {
        configureGrid();
    }

    private void configureGrid() {
        removeAllColumns();

        addColumn(adj -> adj.getTimestamp().format(dtf))
                .setHeader("Timestamp")
                .setAutoWidth(true);

        addColumn(adj -> adj.getAdjustedBy().getUsername())
                .setHeader("Adjusted By")
                .setAutoWidth(true);

        addColumn(StockAdjustment::getDelta)
                .setHeader("Delta")
                .setAutoWidth(true);

        addColumn(StockAdjustment::getStockAfter)
                .setHeader("Stock After")
                .setAutoWidth(true);

        setWidthFull();
        setItems(Collections.emptyList());
    }
}

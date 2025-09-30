package be.ucll.ui.view.Analytics;

import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.domain.model.Product;
import be.ucll.domain.service.ProductService;
import be.ucll.domain.service.StockAdjustmentService;
import be.ucll.ui.component.AppLayoutTemplate;
import be.ucll.ui.component.JFreeChartComponent;
import be.ucll.ui.view.ViewContractLD;
import be.ucll.util.AppRoutes;
import be.ucll.util.ChartUtil;
import be.ucll.util.RoleConstants;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Route(AppRoutes.STOCKADJUSTMENT_ANALYTICSVIEW)
@PageTitle("Stock Analysis")
@RolesAllowed({RoleConstants.ROLE_ADMIN,RoleConstants.ROLE_MANAGER})
@CssImport("./styles/stockanalyticsbody.css")
public class StockAnalyticsView extends AppLayoutTemplate implements ViewContractLD {

    @Autowired
    private ProductService productService;
    @Autowired
    private StockAdjustmentService stockAdjustmentService;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        setBody(buildLayout());
    }

    @Override
    public VerticalLayout buildLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);

        layout.add(buildKpiRow(),lowStockProductsGrid(), buildChartsRow());
        layout.setClassName("StockAnalyticsBody");
        return layout;
    }

    private HorizontalLayout buildKpiRow() {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(JustifyContentMode.CENTER);
        row.setSpacing(true);

        ProductResponseDto dto = productService.mostAdjustedProduct();

        row.add(createKpiCard("Total StockAdjustments", stockAdjustmentService.getTotalStockAdjustments()));
        row.add(createKpiCard("Total Stock",  productService.totalStock()));
        row.add(createKpiCard("Average Stock", String.format("%.2f", productService.getAverageStockLevel())));
        row.add(createKpiCard("Most Adjusted Product", dto.getName(),
                _ -> {
                    getUI().ifPresent(ui -> ui.navigate("product/" + dto.getId()));
                }));
        row.add(createKpiCard("Most Adjusting User" , stockAdjustmentService.getMostActiveUserByAdjustments().getUsername()));
        return row;
    }

    private Div createKpiCard(String title, Object value) {
        Div card = new Div();
        card.addClassName("kpi-card");
        card.add(new H3(title), new H1(value.toString()));
        return card;
    }
    private Div createKpiCard(String title, Object value, ComponentEventListener<ClickEvent<Div>> clickListener) {
        Div card = new Div();
        card.addClassName("kpi-card");
        card.add(new H3(title), new H1(value.toString()));

        if (clickListener != null) {
            card.addClickListener(clickListener);
            card.getStyle().set("cursor", "pointer");
        }

        return card;
    }

    private VerticalLayout buildChartsRow() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        layout.add(
                createAdjustmentsPerProductBarChart(),
                createProductStockVsTotalDeltaScatterPlot(),
                //createAdjustmentsOverTimeLineChart(),
                createStockoutsOverTimeAreaChart()
        );

        return layout;
    }

    public JFreeChartComponent createAdjustmentsPerProductBarChart() {
        Map<Product, Long> data = stockAdjustmentService.getAdjustmentCountsPerProduct();
        Map<String, Number> dataset = data.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getName(), Map.Entry::getValue));

        JFreeChart chart = ChartUtil.createBarChart(
                "Adjustments per Product", "Product", "Adjustments",
                ChartUtil.toCategoryDataset(dataset, "Adjustments")
        );
        return new JFreeChartComponent(chart, 800, 400);
    }

    public JFreeChartComponent createAdjustmentsOverTimeLineChart() {
        Map<LocalDateTime, Long> data = stockAdjustmentService.getAdjustmentsOverTime();

        Map<YearMonth, Number> monthlyData = data.entrySet().stream()
                .collect(Collectors.groupingBy(
                        e -> YearMonth.from(e.getKey()),
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(
                                Collectors.summingLong(Map.Entry::getValue),
                                l -> (Number) l
                        )
                ));

        JFreeChart chart = ChartUtil.createTimeSeriesChart(
                "Stock Adjustments Over Time (Monthly)", "Date", "Adjustments",
                ChartUtil.toXYDataset(monthlyData, "Total Adjustments")
        );
        return new JFreeChartComponent(chart, 800, 400);
    }

    public JFreeChartComponent createProductStockVsTotalDeltaScatterPlot() {
        Map<Product, Integer> totalDeltaPerProduct = stockAdjustmentService.getTotalDeltaPerProduct();

        XYSeries series = new XYSeries("Stock vs. Net Delta");

        totalDeltaPerProduct.forEach((product, delta) -> {
            series.add(product.getStock(), delta);
        });

        XYSeriesCollection dataset = new XYSeriesCollection(series);

        JFreeChart chart = ChartUtil.createScatterPlot(
                "Product Stock vs. Total Net Stock Change",
                "Current Stock Level",
                "Total Net Stock Change (Delta)",
                dataset
        );
        return new JFreeChartComponent(chart, 800, 400);
    }

    //TODO: something is off revisit
    public JFreeChartComponent createStockoutsOverTimeAreaChart() {
        Map<LocalDateTime, Long> stockoutsPerDay = stockAdjustmentService.getStockoutsOverTime();

        Map<String, Number> datasetMap = stockoutsPerDay.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toLocalDate().toString(),
                        Map.Entry::getValue,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));

        DefaultCategoryDataset dataset = ChartUtil.toCategoryDataset(datasetMap, "Stockouts");

        JFreeChart chart = ChartUtil.createAreaChart(
                "Daily Stockouts Over Time",
                "Date",
                "Number of Stockouts",
                dataset
        );
        return new JFreeChartComponent(chart, 800, 400);
    }


    private VerticalLayout lowStockProductsGrid() {
        VerticalLayout lowStockProductsGrid = new VerticalLayout();
        lowStockProductsGrid.setSizeFull();
        lowStockProductsGrid.setMargin(true);
        lowStockProductsGrid.add(new H3("Low Stock Products"));
        Grid<Product> grid = new Grid<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        grid.removeAllColumns();
        grid.addColumn(Product::getId).setHeader("Product Id");
        grid.addColumn(Product::getName).setHeader("Name");
        grid.addColumn(Product::getDescription).setHeader("Description");
        grid.addColumn(Product::getStock).setHeader("Amount In Stock");
        grid.addColumn(product -> product.getCreatedAt() != null
                ? product.getCreatedAt().format(formatter)
                : "")
                .setHeader("Created At");
        grid.addComponentColumn(product -> {
            Button detailButton = new Button("Details", event -> {
                getUI().ifPresent(ui -> ui.navigate("product/" + product.getId()));
            });
            return detailButton;
        }).setHeader("Details");

        grid.setWidthFull();
        grid.setItems(stockAdjustmentService.getLowStockProducts(10));

        lowStockProductsGrid.add(grid);

        return  lowStockProductsGrid;
    }

    @Override
    public void subscribeEventListeners() {

    }
}

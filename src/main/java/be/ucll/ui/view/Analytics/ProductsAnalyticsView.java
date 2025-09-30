package be.ucll.ui.view.Analytics;

import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.domain.model.Product;
import be.ucll.domain.service.ProductService;
import be.ucll.ui.component.AppLayoutTemplate;
import be.ucll.ui.component.JFreeChartComponent;
import be.ucll.ui.view.ViewContractLD;
import be.ucll.util.AppRoutes;
import be.ucll.util.ChartUtil;
import be.ucll.util.RoleConstants;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dependency.CssImport;
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
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Route(AppRoutes.PRODUCT_ANALYTICSVIEW)
@PageTitle("Products Analysis")
@RolesAllowed({RoleConstants.ROLE_ADMIN,RoleConstants.ROLE_MANAGER})
@CssImport("./styles/productsanalyticsbody.css")
public class ProductsAnalyticsView extends AppLayoutTemplate implements ViewContractLD {

    @Autowired
    private ProductService productService;

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

        layout.add(buildKpiRow(), buildChartsRow());
        layout.setClassName("ProductsAnalyticsBody");
        return layout;
    }

    private HorizontalLayout buildKpiRow() {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(JustifyContentMode.CENTER);
        row.setSpacing(true);

        ProductResponseDto dto = productService.mostAdjustedProduct();

        row.add(createKpiCard("Total Products", productService.totalProducts()));
        row.add(createKpiCard("Total Stock",  productService.totalStock()));
        row.add(createKpiCard("Average Stock", String.format("%.2f", productService.getAverageStockLevel())));
        row.add(createKpiCard("Most Adjusted Product", dto.getName(),
                _ -> {
                    getUI().ifPresent(ui -> ui.navigate("product/" + dto.getId()));
                }));
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
                createProductsOverTimeChart(),
                createTopProductsByStockChart(),
                createAdjustmentCountsChart()
        );

        return layout;
    }

    private JFreeChartComponent createProductsOverTimeChart() {
        TimeSeries series = new TimeSeries("Products Created");
        productService.getProductCreationCountOverTime().forEach((dateTime, count) -> {
            series.add(new Day(dateTime.getDayOfMonth(), dateTime.getMonthValue(), dateTime.getYear()), count);
        });
        TimeSeriesCollection dataset = new TimeSeriesCollection(series);

        JFreeChart chart = ChartUtil.createLineChart("Products Created Over Time", "Date", "Count", dataset);

        return new JFreeChartComponent(chart, 800, 400);
    }

    private JFreeChartComponent createTopProductsByStockChart() {
        Map<String, Number> stockData = new LinkedHashMap<>();
        productService.getTopProductsByStock(5).forEach(p -> stockData.put(p.getName(), p.getStock()));

        DefaultPieDataset dataset = ChartUtil.toPieDataset(stockData);
        JFreeChart chart = ChartUtil.createPieChart("Top 5 Products by Stock", dataset);

        return new JFreeChartComponent(chart, 800, 400);
    }

    private JFreeChartComponent createAdjustmentCountsChart() {
        List<Map.Entry<Product, Long>> allAdjustments = new ArrayList<>(productService.getAdjustmentCountsPerProduct()
                .entrySet());

        allAdjustments.sort(Map.Entry.<Product, Long>comparingByValue().reversed());

        Map<String, Number> adjustmentData = allAdjustments.stream()
                .limit(10)
                .collect(LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey().getName(), entry.getValue()),
                        Map::putAll);


        DefaultCategoryDataset dataset = ChartUtil.toCategoryDataset(adjustmentData, "Adjustments");
        JFreeChart chart = ChartUtil.createHorizontalBarChart(
                "Top 10 Adjustments per Product", "Count", "Product", dataset);

        return new JFreeChartComponent(chart, 800, 400);
    }

    @Override
    public void subscribeEventListeners() {

    }
}

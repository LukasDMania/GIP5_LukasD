package be.ucll.ui.view.Analytics;

import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.domain.service.StockAdjustmentService;
import be.ucll.domain.service.impl.UserServiceImpl;
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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Route(AppRoutes.USER_ANALYTICSVIEW)
@PageTitle("User Analysis")
@RolesAllowed({RoleConstants.ROLE_ADMIN,RoleConstants.ROLE_MANAGER})
@CssImport("./styles/useranalyticsbody.css")
public class UserAnalyticsView extends AppLayoutTemplate implements ViewContractLD {

    @Autowired
    private StockAdjustmentService stockAdjustmentService;
    @Autowired
    private UserServiceImpl userService;


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        setBody(buildLayout());
    }

    @Override
    public VerticalLayout buildLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        layout.add(buildKpiRow(), buildChartsRow());
        layout.setClassName("UserAnalyticsBody");
        return layout;
    }

    private HorizontalLayout buildKpiRow() {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        row.setSpacing(true);


        row.add(createKpiCard("Total Users", userService.userCount()));
        row.add(createKpiCard("Active Users", stockAdjustmentService.getTotalActiveUsers()));
        row.add(createKpiCard("Most Adjusting User", stockAdjustmentService.getMostActiveUserByAdjustments().getUsername()));
        row.add(createKpiCard("Avg Adjustments/User", String.format("%.2f", stockAdjustmentService.getAverageAdjustments())));
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
        layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        layout.add(
                //add graphs
                createAdjustmentsPerUserBarChart(),
                createAverageAdjustmentSizePerUserScatterPlot()
        );

        return layout;
    }

    public JFreeChartComponent createAdjustmentsPerUserBarChart() {
        Map<String, Long> adjustmentsPerUser = stockAdjustmentService.getAdjustmentsPerUser();


        Map<String, Number> datasetMap = adjustmentsPerUser.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> (Number) e.getValue(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));


        JFreeChart chart = ChartUtil.createBarChart(
                "Adjustments per User",
                "User",
                "Adjustments",
                ChartUtil.toCategoryDataset(datasetMap, "Adjustments")
        );

        return new JFreeChartComponent(chart, 800, 400);
    }

    public JFreeChartComponent createAverageAdjustmentSizePerUserScatterPlot() {
        Map<String, Double> avgAdjustmentSize = stockAdjustmentService.getAverageAdjustmentSizePerUser();

        XYSeries series = new XYSeries("Avg Adjustment Size per User");
        int i = 1;
        for (Map.Entry<String, Double> entry : avgAdjustmentSize.entrySet()) {
            series.add(i++, entry.getValue());
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);

        JFreeChart chart = ChartUtil.createScatterPlot(
                "Average Adjustment Size per User",
                "User Index",
                "Average Adjustment Size (delta)",
                dataset
        );

        return new JFreeChartComponent(chart, 800, 400);
    }



    @Override
    public void subscribeEventListeners() {

    }
}

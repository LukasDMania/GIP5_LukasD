package be.ucll.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.AreaRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.time.YearMonth;
import java.util.Map;

public class ChartUtil {

    // ---------- DATASET HELPERS ----------

    public static DefaultCategoryDataset toCategoryDataset(Map<String, Number> data, String seriesName) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((category, value) -> dataset.addValue(value, seriesName, category));
        return dataset;
    }

    public static DefaultCategoryDataset toStackedCategoryDataset(Map<String, Map<String, Number>> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((category, seriesMap) -> {
            seriesMap.forEach((series, value) ->
                    dataset.addValue(value, series, category)
            );
        });
        return dataset;
    }

    public static DefaultPieDataset toPieDataset(Map<String, Number> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        data.forEach(dataset::setValue);
        return dataset;
    }

    public static XYSeriesCollection toXYDataset(Map<YearMonth, Number> data, String seriesName) {
        XYSeries series = new XYSeries(seriesName);
        data.forEach((ym, value) -> series.add(ym.atDay(1).toEpochDay(), value.doubleValue()));
        return new XYSeriesCollection(series);
    }


    public static JFreeChart createBarChart(String title, String xAxis, String yAxis,
                                            DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                title,
                xAxis,
                yAxis,
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        styleBarChart(chart);
        return chart;
    }

    public static JFreeChart createHorizontalBarChart(String title, String xAxis, String yAxis,
                                                      DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                title,
                xAxis,
                yAxis,
                dataset,
                PlotOrientation.HORIZONTAL,
                false,
                true,
                false
        );
        styleBarChart(chart);
        return chart;
    }

    public static JFreeChart createStackedBarChart(String title, String xAxis, String yAxis,
                                                   DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createStackedBarChart(
                title,
                xAxis,
                yAxis,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        styleStackedBarChart(chart);
        return chart;
    }

    public static JFreeChart createPieChart(String title, DefaultPieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
                title,
                dataset,
                true,
                true,
                false
        );
        stylePieChart(chart);
        return chart;
    }

    public static JFreeChart createLineChart(String title, String xAxis, String yAxis,
                                             XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                title, xAxis, yAxis, dataset,
                PlotOrientation.VERTICAL, true, true, false
        );
        styleLineChart(chart);
        return chart;
    }

    public static JFreeChart createAreaChart(String title, String xAxis, String yAxis,
                                             CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createAreaChart(
                title, xAxis, yAxis, dataset,
                PlotOrientation.VERTICAL, true, true, false
        );
        styleAreaChart(chart);
        return chart;
    }

    public static JFreeChart createStackedAreaChart(String title, String xAxis, String yAxis,
                                                    CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createStackedAreaChart(
                title, xAxis, yAxis, dataset,
                PlotOrientation.VERTICAL, true, true, false
        );
        styleAreaChart(chart);
        return chart;
    }

    public static JFreeChart createScatterPlot(String title, String xAxis, String yAxis,
                                               XYDataset dataset) {
        JFreeChart chart = ChartFactory.createScatterPlot(
                title, xAxis, yAxis, dataset,
                PlotOrientation.VERTICAL, true, true, false
        );
        styleScatterChart(chart);
        return chart;
    }

    public static JFreeChart createTimeSeriesChart(String title, String xAxis, String yAxis,
                                                   XYDataset dataset) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title, xAxis, yAxis, dataset,
                true, true, false
        );
        styleTimeSeriesChart(chart);
        return chart;
    }

    public static JFreeChart createDonutChart(String title, PieDataset dataset) {
        JFreeChart chart = ChartFactory.createRingChart(
                title, dataset, true, true, false
        );
        stylePieChart(chart);
        return chart;
    }

    private static void styleBarChart(JFreeChart chart) {
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new BarRenderer().getBarPainter());
        renderer.setSeriesPaint(0, new ChartColor(79, 129, 189)); // consistent blue tone
        plot.setBackgroundPaint(ChartColor.WHITE);
        plot.setRangeGridlinePaint(ChartColor.GRAY);
    }

    private static void styleStackedBarChart(JFreeChart chart) {
        CategoryPlot plot = chart.getCategoryPlot();
        StackedBarRenderer renderer = new StackedBarRenderer(true);
        renderer.setRenderAsPercentages(false);
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(ChartColor.WHITE);
        plot.setRangeGridlinePaint(ChartColor.GRAY);
    }

    private static void stylePieChart(JFreeChart chart) {
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(ChartColor.WHITE);
        plot.setSectionOutlinesVisible(false);
        plot.setSimpleLabels(true);
    }

    private static void styleLineChart(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, new ChartColor(79, 129, 189));
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(ChartColor.WHITE);
        plot.setRangeGridlinePaint(ChartColor.GRAY);

        if (plot.getDomainAxis() instanceof NumberAxis) {
            plot.setDomainAxis(new DateAxis("Date"));
        }
    }

    private static void styleAreaChart(JFreeChart chart) {
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        AreaRenderer renderer = new AreaRenderer();
        renderer.setSeriesPaint(0, new ChartColor(155, 187, 89)); // green tone
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(ChartColor.WHITE);
        plot.setRangeGridlinePaint(ChartColor.GRAY);
    }

    private static void styleScatterChart(JFreeChart chart) {
        XYPlot plot = (XYPlot) chart.getPlot();
        XYDotRenderer renderer = new XYDotRenderer();
        renderer.setDotWidth(6);
        renderer.setDotHeight(6);
        renderer.setSeriesPaint(0, new ChartColor(192, 80, 77)); // red tone
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(ChartColor.WHITE);
        plot.setRangeGridlinePaint(ChartColor.GRAY);
    }

    private static void styleTimeSeriesChart(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, new ChartColor(79, 129, 189));
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(ChartColor.WHITE);
        plot.setRangeGridlinePaint(ChartColor.GRAY);
        plot.setDomainAxis(new DateAxis("Date"));
    }

}

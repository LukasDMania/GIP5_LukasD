package be.ucll.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import org.jfree.chart.JFreeChart;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

@Tag("div")
public class JFreeChartComponent extends Component {

    private final Image chartImage;

    public JFreeChartComponent(JFreeChart chart, int width, int height) {
        chartImage = new Image();
        chartImage.setWidth(width + "px");
        chartImage.setHeight(height + "px");
        updateImage(chart, width, height);
        getElement().appendChild(chartImage.getElement());
    }

    private void updateImage(JFreeChart chart, int width, int height) {
        try {
            BufferedImage bufferedImage = chart.createBufferedImage(width, height);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());

            StreamResource resource = new StreamResource("chart.png", () -> is);
            chartImage.setSrc(resource);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

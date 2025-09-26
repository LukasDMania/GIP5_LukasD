package be.ucll.ui.component.ProductDetailView;

import be.ucll.application.dto.product.ProductResponseDto;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductDetailCard extends VerticalLayout {

    private final Logger LOG =  LoggerFactory.getLogger(ProductDetailCard.class);

    private final Span productId = new Span();
    private final Span productName = new Span();
    private final Span productStock = new Span();
    private final Span productDescription = new Span();
    private final Span productCreatedAt = new Span();
    private final Button backButton = new Button("Back");



    public ProductDetailCard() {
        setSizeFull();
        setSpacing(true);

        H2 title = new H2("Product Details");

        backButton.addClickListener(e -> fireEvent(new BackEvent(this)));

        add(title, productId, productName, productStock, productDescription, productCreatedAt, backButton);
    }

    public void setProductDetails(ProductResponseDto productResponseDto) {
        productId.setText(productResponseDto.getId().toString());
        productName.setText(productResponseDto.getName());
        productStock.setText(String.valueOf(productResponseDto.getStock()));
        productDescription.setText(productResponseDto.getDescription());
        productCreatedAt.setText(productResponseDto.getCreatedAt().toString());
    }

    public static class BackEvent extends ComponentEvent<ProductDetailCard> {
        public BackEvent(ProductDetailCard source) {
            super(source, false);
        }
    }

    public Registration addBackListener(ComponentEventListener<BackEvent> listener) {
        return addListener(BackEvent.class, listener);
    }
}

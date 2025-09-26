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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class ProductDetailCard extends VerticalLayout {

    private final Logger LOG =  LoggerFactory.getLogger(ProductDetailCard.class);

    private final Span productId = new Span();
    private final Span productName = new Span();
    private final Span productStock = new Span();
    private final Span productDescription = new Span();
    private final Span productCreatedAt = new Span();
    private final Button backButton = new Button("Back");
    private final Button editButton = new Button("Edit");

    public ProductDetailCard() {
        setSizeFull();
        setSpacing(true);

        H2 title = new H2("Product Details");

        backButton.addClickListener(e -> fireEvent(new BackEvent(this)));

        if (userHasRole("ROLE_ADMIN") || userHasRole("ROLE_MANAGER")) {
            editButton.addClickListener(e -> fireEvent(new EditEvent(this)));
            add(title, productId, productName, productStock, productDescription, productCreatedAt, backButton, editButton);
        } else {
            add(title, productId, productName, productStock, productDescription, productCreatedAt, backButton);
        }
    }


    public void setProductDetails(ProductResponseDto productResponseDto) {
        productId.setText(productResponseDto.getId().toString());
        productName.setText(productResponseDto.getName());
        productStock.setText(String.valueOf(productResponseDto.getStock()));
        productDescription.setText(productResponseDto.getDescription());
        productCreatedAt.setText(productResponseDto.getCreatedAt().toString());
    }

    private boolean userHasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority(role));
    }


    public static class BackEvent extends ComponentEvent<ProductDetailCard> {
        public BackEvent(ProductDetailCard source) {
            super(source, false);
        }
    }
    public static class EditEvent extends ComponentEvent<ProductDetailCard> {
        public EditEvent(ProductDetailCard source) {
            super(source, false);
        }
    }

    public Registration addBackListener(ComponentEventListener<BackEvent> listener) {
        return addListener(BackEvent.class, listener);
    }
    public Registration addEditListener(ComponentEventListener<EditEvent> listener) {
        return addListener(EditEvent.class, listener);
    }
}

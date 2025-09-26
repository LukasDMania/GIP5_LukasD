package be.ucll.ui.view;

import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.application.dto.product.ProductUpdateRequestDto;
import be.ucll.application.dto.stockadjustment.StockAdjustmentRequestDto;
import be.ucll.application.dto.stockadjustment.StockAdjustmentResponseDto;
import be.ucll.domain.model.StockAdjustment;
import be.ucll.domain.model.User;
import be.ucll.domain.service.ProductService;
import be.ucll.domain.service.StockAdjustmentService;
import be.ucll.domain.service.impl.UserServiceImpl;
import be.ucll.exception.product.ProductNotFoundException;
import be.ucll.ui.component.AppLayoutTemplate;
import be.ucll.ui.component.ProductDetailView.ProductDetailCard;
import be.ucll.ui.component.ProductDetailView.StockAdjustmentGrid;
import be.ucll.ui.component.ProductDetailView.StockEditorForm;
import be.ucll.util.AppRoutes;
import be.ucll.util.NotificationUtil;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@Route(AppRoutes.PRODUCT_VIEW)
@PageTitle("Product Details")
@RolesAllowed("USER")
public class ProductDetailView extends AppLayoutTemplate implements BeforeEnterObserver, ViewContractLD {

    private static final Logger LOG = LoggerFactory.getLogger(ProductDetailView.class);

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private StockAdjustmentService  stockAdjustmentService;

    private ProductDetailCard productDetailCard;
    private StockEditorForm stockEditorForm;
    private StockAdjustmentGrid stockAdjustmentGrid;

    private ProductResponseDto currentProduct;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        setBody(buildLayout());
        subscribeEventListeners();
        refreshData();
    }

    @Override
    public VerticalLayout buildLayout() {
        productDetailCard = new ProductDetailCard();
        stockEditorForm = new StockEditorForm(currentProduct.getStock());
        stockAdjustmentGrid = new StockAdjustmentGrid();

        VerticalLayout layout = new VerticalLayout(productDetailCard,  stockEditorForm, stockAdjustmentGrid);
        layout.setSizeFull();
        return layout;
    }

    @Override
    public void subscribeEventListeners() {
        stockEditorForm.addStockAdjustListener(event -> {
            StockAdjustmentRequestDto req = new StockAdjustmentRequestDto(
                    currentProduct.getId(),
                    event.getDelta(),
                    getCurrentUser().getUsername()
            );
            try {
                StockAdjustmentResponseDto resp = productService.adjustStock(req);
                refreshData();
                NotificationUtil.showNotification("Stock updated", 2000);
            } catch (Exception ex) {
                NotificationUtil.showNotification("Failed to update stock: " + ex.getMessage(), 3000);
            }
        });

        productDetailCard.addBackListener(_ -> {
            getUI().ifPresent(ui -> ui.navigate(AppRoutes.DASHBOARD_VIEW));
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> maybeId = event.getRouteParameters().get("id").map(Long::valueOf);
        if (maybeId.isEmpty()) {
            LOG.warn("ProductDetailView accessed without ID redirect to dashboard");
            event.forwardTo(AppRoutes.DASHBOARD_VIEW);
            return;
        }

        try {
            currentProduct = productService.findById(maybeId.get());
        } catch (AccessDeniedException ex) {
            LOG.warn("Access denied while loading product {}", maybeId.get(), ex);
            NotificationUtil.showNotification("No Rights", 3000);
            event.forwardTo(AppRoutes.DASHBOARD_VIEW);
            return;
        } catch (ProductNotFoundException ex) {
            LOG.warn("Product {} not found", maybeId.get(), ex);
            NotificationUtil.showNotification("Product not found.", 3000);
            event.forwardTo(AppRoutes.DASHBOARD_VIEW);
            return;
        }
    }

    private void refreshData() {
        currentProduct = productService.findById(currentProduct.getId());
        stockEditorForm.setCurrentStock(currentProduct.getStock());
        productDetailCard.setProductDetails(currentProduct);

        List<StockAdjustment> adjustmentsOfProduct = stockAdjustmentService.findByProduct(productService.getProductById(currentProduct.getId()));
        stockAdjustmentGrid.setItems(adjustmentsOfProduct);
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        String username = auth.getName();
        return userService.getDomainUserByUsername(username);
    }

}

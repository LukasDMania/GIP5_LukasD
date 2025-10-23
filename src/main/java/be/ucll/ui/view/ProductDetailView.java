package be.ucll.ui.view;

import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.application.dto.stockadjustment.StockAdjustmentRequestDto;
import be.ucll.application.dto.stockadjustment.StockAdjustmentResponseDto;
import be.ucll.domain.model.StockAdjustment;
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
import be.ucll.util.RoleConstants;
import be.ucll.util.UserUtil;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Route(AppRoutes.PRODUCT_VIEW)
@PageTitle("Product Details")
@RolesAllowed({RoleConstants.ROLE_ADMIN, RoleConstants.ROLE_MANAGER, RoleConstants.ROLE_USER})
public class ProductDetailView extends AppLayoutTemplate implements BeforeEnterObserver, ViewContractLD {

    private static final Logger LOG = LoggerFactory.getLogger(ProductDetailView.class);

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private StockAdjustmentService stockAdjustmentService;

    @Autowired
    private UserUtil userUtil;

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
        HorizontalLayout horizontalLayout = new HorizontalLayout(productDetailCard, stockEditorForm);
        VerticalLayout layout = new VerticalLayout(horizontalLayout, stockAdjustmentGrid);
        layout.setSizeFull();
        layout.setPadding(true);
        layout.setSpacing(true);
        return layout;
    }

    @Override
    public void subscribeEventListeners() {
        stockEditorForm.addStockAdjustListener(event -> {
            StockAdjustmentRequestDto req = new StockAdjustmentRequestDto(
                    currentProduct.getId(),
                    event.getDelta(),
                    userUtil.getCurrentUser().getUsername()
            );
            try {
                productService.adjustStock(req);
                refreshData();
                NotificationUtil.showNotification("Stock updated", 2000);
            } catch (Exception ex) {
                NotificationUtil.showNotification("Failed to update stock: " + ex.getMessage(), 3000);
            }
        });

        productDetailCard.addBackListener(_ -> getUI().ifPresent(ui -> ui.navigate(AppRoutes.DASHBOARD_VIEW)));
        productDetailCard.addEditListener(_ -> getUI().ifPresent(ui -> ui.navigate("product/edit/" + currentProduct.getId())));
        productDetailCard.addDeleteListener(_ -> {
            productService.deleteProduct(currentProduct.getId());
            NotificationUtil.showNotification("Product deleted", 2000);
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
        } catch (Exception ex) {
            NotificationUtil.showNotification("Product not found or access denied.", 3000);
            event.forwardTo(AppRoutes.DASHBOARD_VIEW);
        }
    }

    private void refreshData() {
        currentProduct = productService.findById(currentProduct.getId());
        stockEditorForm.setCurrentStock(currentProduct.getStock());
        productDetailCard.setProductDetails(currentProduct);
        List<StockAdjustment> adjustments = stockAdjustmentService.findByProduct(productService.getProductById(currentProduct.getId()));
        stockAdjustmentGrid.setItems(adjustments);
    }
}

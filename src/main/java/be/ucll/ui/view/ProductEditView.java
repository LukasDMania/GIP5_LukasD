package be.ucll.ui.view;

import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.domain.service.ProductService;
import be.ucll.exception.product.ProductNotFoundException;
import be.ucll.ui.component.AppLayoutTemplate;
import be.ucll.ui.component.ProductEditView.ProductEditForm;
import be.ucll.util.AppRoutes;
import be.ucll.util.NotificationUtil;
import be.ucll.util.RoleConstants;
import be.ucll.util.UserUtil;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route(AppRoutes.PRODUCT_EDIT_VIEW)
@PageTitle("Product Edit")
@RolesAllowed({RoleConstants.ROLE_ADMIN,RoleConstants.ROLE_MANAGER, RoleConstants.ROLE_USER})
public class ProductEditView extends AppLayoutTemplate implements BeforeEnterObserver, ViewContractLD {

    private final Logger LOG = LoggerFactory.getLogger(ProductEditView.class);

    @Autowired
    private ProductService productService;
    @Autowired
    private UserUtil  userUtil;

    private ProductEditForm productEditForm;

    private ProductResponseDto currentProduct;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        setBody(buildLayout());
        subscribeEventListeners();
    }

    @Override
    public VerticalLayout buildLayout() {
        productEditForm = new ProductEditForm();
        productEditForm.setProduct(currentProduct);

        VerticalLayout layout = new VerticalLayout();
        layout.add(productEditForm);

        return layout;
    }

    @Override
    public void subscribeEventListeners() {
        productEditForm.addCancelListener(_ -> {
            UI.getCurrent().navigate(DashboardView.class);
        });
        productEditForm.addSaveListener(event -> {
            currentProduct = productService.updateProduct(event.getProductUpdateRequestDto(), userUtil.getCurrentUser().getUsername());
            productEditForm.setProduct(currentProduct);
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> maybeId = event.getRouteParameters().get("id").map(Long::valueOf);
        if (maybeId.isEmpty()) {
            LOG.warn("ProductEditView accessed without ID redirect to dashboard");
            event.forwardTo(AppRoutes.DASHBOARD_VIEW);
            return;
        }

        try {
            currentProduct = productService.findById(maybeId.get());
        } catch (AccessDeniedException ex) {
            LOG.warn("Access denied while loading product {}", maybeId.get(), ex);
            NotificationUtil.showNotification("No Rights", 3000);
            event.forwardTo(AppRoutes.DASHBOARD_VIEW);
        } catch (ProductNotFoundException ex) {
            LOG.warn("Product {} not found", maybeId.get(), ex);
            NotificationUtil.showNotification("Product not found.", 3000);
            event.forwardTo(AppRoutes.DASHBOARD_VIEW);
        }

    }
}

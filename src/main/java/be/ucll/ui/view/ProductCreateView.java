package be.ucll.ui.view;

import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.domain.service.ProductService;
import be.ucll.ui.component.AppLayoutTemplate;
import be.ucll.ui.component.CreateProductForm;
import be.ucll.util.AppRoutes;
import be.ucll.util.RoleConstants;
import be.ucll.util.UserUtil;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@Route(AppRoutes.PRODUCT_CREATE_VIEW)
@PageTitle("Create Product")
@RolesAllowed({RoleConstants.ROLE_ADMIN, RoleConstants.ROLE_MANAGER})
public class ProductCreateView extends AppLayoutTemplate implements ViewContractLD {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserUtil userUtil;

    private CreateProductForm createProductForm;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        setBody(buildLayout());
        subscribeEventListeners();
    }

    @Override
    public VerticalLayout buildLayout() {
        createProductForm = new CreateProductForm();
        VerticalLayout layout = new VerticalLayout(createProductForm);
        return layout;
    }

    @Override
    public void subscribeEventListeners() {
        createProductForm.addListener(CreateProductForm.SaveEvent.class, event -> {
            ProductResponseDto createdProduct = productService.createProduct(event.getProduct(), userUtil.getCurrentUsername());
            getUI().ifPresent(ui -> ui.navigate("product/" + createdProduct.getId()));
        });
    }
}

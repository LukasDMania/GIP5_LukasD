package be.ucll.ui.view;

import be.ucll.application.dto.SearchCriteriaDto;
import be.ucll.domain.model.Product;
import be.ucll.domain.service.ProductService;
import be.ucll.domain.service.impl.SearchHistoryService;
import be.ucll.ui.component.AppLayoutTemplate;
import be.ucll.ui.component.ProductGrid;
import be.ucll.ui.component.SearchForm;
import be.ucll.util.AppRoutes;
import be.ucll.util.RoleConstants;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

@Route(AppRoutes.DASHBOARD_VIEW)
@PageTitle("Login")
@RolesAllowed({RoleConstants.ROLE_USER, RoleConstants.ROLE_ADMIN})
public class DashboardView extends AppLayoutTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardView.class);

    @Autowired
    private SearchHistoryService searchHistoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private SearchForm searchForm;
    private ProductGrid productGrid;
    public DashboardView() {
        LOG.info("DashboardView initialized");
    }

    @PostConstruct
    private void init() {
        setBody(buildDashboardLayout());
        restoreSession();
        searchForm.setHistoryComboBoxItems(searchHistoryService.loadHistory());
    }

    private VerticalLayout buildDashboardLayout() {
        productGrid = new ProductGrid();
        searchForm = new SearchForm(productService, searchHistoryService, applicationEventPublisher);

        VerticalLayout layout = new VerticalLayout(searchForm, productGrid);
        layout.setSizeFull();
        layout.setPadding(true);
        layout.setSpacing(true);

        return layout;
    }

    private void restoreSession() {
        SearchCriteriaDto savedCriteria = (SearchCriteriaDto) VaadinSession.getCurrent().getAttribute("lastSearchCriteria");
        List<Product> savedResults = (List<Product>) VaadinSession.getCurrent().getAttribute("lastSearchResults");

        if (savedCriteria != null && savedResults != null) {
            searchForm.loadCriteria(savedCriteria);
            productGrid.setItems(savedResults);
        }
    }
}

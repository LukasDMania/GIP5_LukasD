package be.ucll.ui.view;

import be.ucll.application.dto.SearchCriteriaDto;
import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.application.events.SearchHistoryChangedEvent;
import be.ucll.application.mapper.product.ProductMapper;
import be.ucll.domain.model.Product;
import be.ucll.domain.service.ProductService;
import be.ucll.domain.service.impl.SearchHistoryService;
import be.ucll.ui.component.AppLayoutTemplate;
import be.ucll.ui.component.ProductGrid;
import be.ucll.ui.component.SearchForm;
import be.ucll.util.AppRoutes;
import be.ucll.util.RoleConstants;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Route(AppRoutes.DASHBOARD_VIEW)
@PageTitle("Login")
@RolesAllowed({RoleConstants.ROLE_ADMIN, RoleConstants.ROLE_USER})
@Component
public class DashboardView extends AppLayoutTemplate implements ViewContractLD{

    private static final Logger LOG = LoggerFactory.getLogger(DashboardView.class);

    @Autowired
    private SearchHistoryService searchHistoryService;

    @Autowired
    private ProductService productService;

    private SearchForm searchForm;
    private ProductGrid productGrid;

    public DashboardView() {
        LOG.info("DashboardView initialized");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        LOG.info("DashboardView onAttach");

        setBody(buildLayout());
        subscribeEventListeners();
        restoreSession();
        searchForm.setHistoryComboBoxItems(searchHistoryService.loadHistory());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            System.out.println("User: " + authentication.getName());
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                System.out.println("Role: " + authority.getAuthority());
            }
        } else {
            System.out.println("No authenticated user");
        }
    }

    @Override
    public VerticalLayout buildLayout() {
        productGrid = new ProductGrid();
        searchForm = new SearchForm(productService, searchHistoryService);

        VerticalLayout layout = new VerticalLayout(searchForm, productGrid);
        layout.setSizeFull();
        layout.setPadding(true);
        layout.setSpacing(true);

        return layout;
    }

    @Override
    public void subscribeEventListeners() {
        searchForm.addListener(SearchForm.SearchEvent.class, event -> {
            handleSearch(event.getCriteria());
        });

        searchForm.addListener(SearchForm.ClearEvent.class, _ -> {
            productGrid.setItems(Collections.emptyList());
        });
    }

    //Spring event listeners
    @EventListener
    public void onSearchHistoryChanged(SearchHistoryChangedEvent event) {
        LOG.info("onSearchHistoryChanged");
        getUI().ifPresent(ui -> ui.access(() -> {
            searchForm.setHistoryComboBoxItems(event.history());
        }));
    }

    private void restoreSession() {
        SearchCriteriaDto savedCriteria = (SearchCriteriaDto) VaadinSession.getCurrent().getAttribute("lastSearchCriteria");
        List<Product> savedResults = (List<Product>) VaadinSession.getCurrent().getAttribute("lastSearchResults");

        if (savedCriteria != null && savedResults != null) {
            searchForm.loadCriteria(savedCriteria);

            List<ProductResponseDto> dtos =  new ArrayList<>();
            for (Product product : savedResults) {
                dtos.add(ProductMapper.toResponseDto(product));
            }
            productGrid.setItems(dtos);
        }
    }

    private void handleSearch(SearchCriteriaDto criteria) {
        List<ProductResponseDto> results = productService.searchProductsByCriteriaAndPublish(criteria);
        productGrid.setItems(results);

        searchHistoryService.addToHistory(criteria);

        VaadinSession.getCurrent().setAttribute("lastSearchCriteria", criteria);
        VaadinSession.getCurrent().setAttribute("lastSearchResults", results);

        if (results.isEmpty()) {
            showNotification("Geen resultaten gevonden.");
        } else {
            showNotification(results.size() + " resultaten gevonden.");
        }
    }

    private void showNotification(String message) {
        Notification notification = new Notification();
        notification.setText(message);
        notification.setDuration(3000);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.open();
    }
}

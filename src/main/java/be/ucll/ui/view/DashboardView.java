package be.ucll.ui.view;

import be.ucll.application.dto.SearchCriteriaDto;
import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.application.events.ClearRequestedEvent;
import be.ucll.application.events.SearchHistoryChangedEvent;
import be.ucll.application.events.SearchRequestedEvent;
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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
@PermitAll
@Component
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

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        LOG.info("DashboardView onAttach");

        setBody(buildDashboardLayout());
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

            List<ProductResponseDto> dtos =  new ArrayList<>();
            for (Product product : savedResults) {
                dtos.add(ProductMapper.toResponseDto(product));
            }
            productGrid.setItems(dtos);
        }
    }

    @EventListener
    public void onSearchRequested(SearchRequestedEvent event) {
        LOG.info("onSearchRequested listener");

        List<ProductResponseDto> productResponseDtos = productService.searchProductsByCriteria(event.searchCriteriaDto());
        productGrid.setItems(productResponseDtos);

        searchHistoryService.addToHistory(event.searchCriteriaDto());

        VaadinSession.getCurrent().setAttribute("lastSearchCriteria", event.searchCriteriaDto());
        VaadinSession.getCurrent().setAttribute("lastSearchResults", productResponseDtos);

        if (productResponseDtos.isEmpty()) {
            Notification.show("Geen resultaten gevonden.", 3000, Notification.Position.MIDDLE);
        } else {
            Notification.show(productResponseDtos.size() + " resultaten gevonden.", 3000, Notification.Position.MIDDLE);
        }
    }

    @EventListener
    public void onClearRequested(ClearRequestedEvent event) {
        productGrid.setItems(Collections.emptyList());
    }

    @EventListener
    public void onSearchHistoryChanged(SearchHistoryChangedEvent event) {
        searchForm.setHistoryComboBoxItems(searchHistoryService.loadHistory());
    }
}

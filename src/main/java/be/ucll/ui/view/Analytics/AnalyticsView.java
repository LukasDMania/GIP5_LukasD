package be.ucll.ui.view.Analytics;

import be.ucll.domain.service.ProductService;
import be.ucll.domain.service.impl.UserServiceImpl;
import be.ucll.ui.component.AppLayoutTemplate;
import be.ucll.ui.view.ViewContractLD;
import be.ucll.util.AppRoutes;
import be.ucll.util.RoleConstants;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@Route(AppRoutes.ANALYTICSVIEW)
@PageTitle("Analytics")
@RolesAllowed({RoleConstants.ROLE_ADMIN, RoleConstants.ROLE_MANAGER})
@CssImport("./styles/analyticsdashboard.css")
public class AnalyticsView extends AppLayoutTemplate implements ViewContractLD {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserServiceImpl userService;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        setBody(buildLayout());
        subscribeEventListeners();
    }

    @Override
    public VerticalLayout buildLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        layout.add(createKpiCardsGrid());
        return layout;
    }

    @Override
    public void subscribeEventListeners() {
    }

    private Component createKpiCardsGrid() {
        Div cardsGrid = new Div();
        cardsGrid.addClassName("cards-grid");

        cardsGrid.add(
                createCard("Products", productService.totalProducts()),
                createCard("Stock", productService.totalStock()),
                createCard("Users", userService.userCount())
        );

        return cardsGrid;
    }

    private Component createCard(String title, Object value) {
        Div card = new Div();
        card.addClassName("kpi-card");
        card.add(new H3(title), new H1(value.toString()));
        card.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("analytics/" + title.toLowerCase().replace(" ", "-")))
        );
        return card;
    }
}

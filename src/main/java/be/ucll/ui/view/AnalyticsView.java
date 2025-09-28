package be.ucll.ui.view;

import be.ucll.ui.component.AppLayoutTemplate;
import be.ucll.util.AppRoutes;
import be.ucll.util.RoleConstants;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(AppRoutes.ANALYTICSVIEW)
@PageTitle("Analytics")
@RolesAllowed({RoleConstants.ROLE_ADMIN,RoleConstants.ROLE_MANAGER})
public class AnalyticsView extends AppLayoutTemplate implements ViewContractLD {


    @Override
    public VerticalLayout buildLayout() {
        VerticalLayout layout = new VerticalLayout();

        layout.setSizeFull();
        layout.add(new H2("ANALYTICS"));
        return layout;
    }

    @Override
    public void subscribeEventListeners() {

    }
}

package be.ucll.ui.view;

import be.ucll.ui.component.AppLayoutTemplate;
import be.ucll.util.AppRoutes;
import be.ucll.util.RoleConstants;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(AppRoutes.DASHBOARD_VIEW)
@PageTitle("Login")
@RolesAllowed({RoleConstants.ROLE_USER, RoleConstants.ROLE_ADMIN})
public class DashboardView extends AppLayoutTemplate {

}

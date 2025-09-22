package be.ucll.ui.view;


import be.ucll.ui.component.AppLayoutTemplate;
import be.ucll.ui.component.AppRoutes;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(AppRoutes.LOGIN_VIEW)
@PageTitle("Login")
@PermitAll
public class LoginView extends AppLayoutTemplate {
}

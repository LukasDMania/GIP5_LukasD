package be.ucll.ui.view;


import be.ucll.application.events.LoginFailedEvent;
import be.ucll.application.events.LoginSucceededEvent;
import be.ucll.domain.service.LoginService;
import be.ucll.ui.component.AppLayoutTemplate;
import be.ucll.ui.component.LoginForm;
import be.ucll.util.AppRoutes;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Route(AppRoutes.LOGIN_VIEW)
@PageTitle("Login")
@PermitAll
@Component
public class LoginView extends AppLayoutTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(LoginView.class);

    @Autowired
    private LoginService loginService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private LoginForm loginForm;

    @PostConstruct
    private void init() {
        setBody(buildLoginLayout());
    }

    private VerticalLayout buildLoginLayout() {
        loginForm = new LoginForm(applicationEventPublisher);

        VerticalLayout layout = new VerticalLayout(loginForm);
        layout.setSizeFull();
        layout.setAlignItems(VerticalLayout.Alignment.CENTER);
        layout.setJustifyContentMode(VerticalLayout.JustifyContentMode.CENTER);

        return layout;
    }

    @EventListener
    public void onLoginSucceeded(LoginSucceededEvent event) {
        UI.getCurrent().navigate(AppRoutes.DASHBOARD_VIEW);
    }

    @EventListener
    public void onLoginFailed(LoginFailedEvent event) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Login Failed");
        dialog.setText("Invalid username or password. Please try again.");
        dialog.setConfirmText("OK");
        dialog.open();
    }
}

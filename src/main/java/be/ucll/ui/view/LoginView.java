package be.ucll.ui.view;


import be.ucll.application.dto.LoginDto;
import be.ucll.domain.service.LoginService;
import be.ucll.ui.component.AppLayoutTemplate;
import be.ucll.ui.component.LoginForm;
import be.ucll.util.AppRoutes;
import com.vaadin.flow.component.AttachEvent;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Route(AppRoutes.LOGIN_VIEW)
@PageTitle("Login")
@PermitAll
public class LoginView extends AppLayoutTemplate implements ViewContractLD {

    private static final Logger LOG = LoggerFactory.getLogger(LoginView.class);

    @Autowired
    private LoginService loginService;

    private LoginForm loginForm;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        setBody(buildLayout());
        subscribeEventListeners();
    }

    @Override
    public VerticalLayout buildLayout() {
        loginForm = new LoginForm();

        VerticalLayout layout = new VerticalLayout(loginForm);
        layout.setSizeFull();
        layout.setAlignItems(VerticalLayout.Alignment.CENTER);
        layout.setJustifyContentMode(VerticalLayout.JustifyContentMode.CENTER);

        return layout;
    }

    @Override
    public void subscribeEventListeners() {
        loginForm.addLoginListener(loginEvent -> {
            handleLogin(loginEvent.getLoginDto());
        });
    }

    private void handleLogin(LoginDto loginDto) {
        try {
            loginService.authenticate(loginDto);
            LOG.info("Login successful for user: {}", loginDto.getUsername());
            Notification.show("Login Successful", 2000, Notification.Position.MIDDLE);
            getUI().ifPresent(ui -> ui.navigate(AppRoutes.DASHBOARD_VIEW));
        } catch (AuthenticationException e) {
            LOG.warn("Login failed for user: {}", loginDto.getUsername());
            loginForm.setErrorMessage("Invalid username or password.");
        }
    }
}

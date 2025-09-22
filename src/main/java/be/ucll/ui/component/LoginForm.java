package be.ucll.ui.component;

import be.ucll.application.dto.LoginDto;
import be.ucll.application.events.LoginRequestedEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.springframework.context.ApplicationEventPublisher;

public class LoginForm extends VerticalLayout {
    private final Binder<LoginDto> binder = new Binder<>(LoginDto.class);
    private final LoginDto loginDto = new LoginDto();

    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final Span errorLabel = new Span();
    private final Button loginButton = new Button("Login");

    private final ApplicationEventPublisher applicationEventPublisher;

    public LoginForm(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
        setWidth("300px");

        errorLabel.getStyle().set("color", "red");
        loginButton.setEnabled(false);

        configureBinder();

        FormLayout formLayout = new FormLayout(username, password, loginButton);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        add(formLayout, errorLabel);

        loginButton.addClickListener(_ -> {
            if (binder.writeBeanIfValid(loginDto)) {
                applicationEventPublisher.publishEvent(
                        new LoginRequestedEvent(loginDto)
                );
            } else {
                setErrorMessage("Ongeldige gebruikersnaam of wachtwoord.");
            }
        });
    }

    private void configureBinder() {
        binder.forField(username)
                .asRequired("Username is required.")
                .bind(LoginDto::getUsername, LoginDto::setUsername);
        binder.forField(password)
                .asRequired("Password is required.")
                .bind(LoginDto::getPassword, LoginDto::setPassword);

        binder.addStatusChangeListener(_ -> loginButton.setEnabled(binder.isValid()));

        binder.addValueChangeListener(_ -> errorLabel.setText(""));
    }

    public void setErrorMessage(String message) {
        errorLabel.setText(message);
    }
}

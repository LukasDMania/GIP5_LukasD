package be.ucll.application.events.handler;

import be.ucll.application.events.LoginFailedEvent;
import be.ucll.application.events.LoginRequestedEvent;
import be.ucll.application.events.LoginSucceededEvent;
import be.ucll.domain.service.LoginService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class LoginEventHandler {

    private final LoginService loginService;
    private final ApplicationEventPublisher publisher;

    public LoginEventHandler(LoginService loginService, ApplicationEventPublisher publisher) {
        this.loginService = loginService;
        this.publisher = publisher;
    }

    @EventListener
    public void handleLogin(LoginRequestedEvent event) {
        boolean success = loginService.authenticate(event.loginDto());

        if (success) {
            publisher.publishEvent(new LoginSucceededEvent(event.loginDto().getUsername()));
        } else {
            publisher.publishEvent(new LoginFailedEvent(event.loginDto().getUsername()));
        }
    }
}


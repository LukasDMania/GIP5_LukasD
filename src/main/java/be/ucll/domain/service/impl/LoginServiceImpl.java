package be.ucll.domain.service.impl;

import be.ucll.application.dto.LoginDto;
import be.ucll.application.events.LoginFailedEvent;
import be.ucll.application.events.LoginSuccessEvent;
import be.ucll.domain.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger LOG = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private ApplicationEventPublisher springEventPublisher;

    @Override
    public boolean authenticate(LoginDto loginDto) {
        try {
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

            Authentication authentication = authenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            httpServletRequest.getSession().setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            springEventPublisher.publishEvent(new LoginSuccessEvent());
            return true;

        } catch (AuthenticationException ex) {
            LOG.warn("Login failed for user '{}': {}", loginDto.getUsername(), ex.getMessage());
            springEventPublisher.publishEvent(new LoginFailedEvent());
            return false;
        }
    }
}

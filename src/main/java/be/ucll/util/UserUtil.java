package be.ucll.util;

import be.ucll.domain.model.User;
import be.ucll.domain.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {
    @Autowired
    private UserServiceImpl userService;

    private User currentUser;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        String username = auth.getName();
        return userService.getDomainUserByUsername(username);
    }

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        return auth.getName();
    }
}

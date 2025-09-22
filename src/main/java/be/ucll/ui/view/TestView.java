package be.ucll.ui.view;

import be.ucll.domain.model.User;
import be.ucll.domain.service.impl.UserServiceImpl;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@Route("test")
@PermitAll
public class TestView extends VerticalLayout {

    @Autowired
    private UserServiceImpl userService;

    private Grid<User> grid;

    @PostConstruct
    private void init() {
        setSizeFull();
        add(new H1("Users"));

        grid = new Grid<>(User.class, false);
        grid.addColumn(User::getUsername).setHeader("Username");
        grid.addColumn(User::getEmail).setHeader("Email");
        grid.addColumn(user ->
                String.join(", ", user.getRoles().stream().map(r -> r.getName()).toList())
        ).setHeader("Roles");

        grid.setItems(userService.findAll());
        add(grid);
    }
}

package be.ucll.ui.component;

import be.ucll.ui.view.Analytics.AnalyticsView;
import be.ucll.ui.view.DashboardView;
import be.ucll.util.RoleConstants;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class AppLayoutTemplate extends VerticalLayout {
    private final Div body = new Div();
    private final Div navLinks = new Div();

    public AppLayoutTemplate() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(buildHeader(), body, buildFooter());

        body.setSizeFull();
        body.getStyle().set("padding", "1rem");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authorities: " +
                auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());

        if (auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> RoleConstants.ROLE_ADMIN.equals(a.getAuthority())
                        || RoleConstants.ROLE_MANAGER.equals(a.getAuthority()))) {
            navLinks.add(new RouterLink("Analytics", AnalyticsView.class));
        }
    }

    private Component buildHeader() {
        Div header = new Div();
        header.setSizeFull();
        header.getStyle()
                .set("padding", "1rem")
                .set("border-bottom", "1px solid #ccc");

        Image logo = new Image("https://dummyimage.com/100x40/000/fff&text=MyStock", "Logo");
        logo.setHeight("40px");

        Span title = new Span("Stock Lookup System LD");
        title.getStyle()
                .set("margin-left", "1rem")
                .set("font-weight", "bold");

        navLinks.getStyle()
                .set("margin-left", "2rem")
                .set("display", "flex")
                .set("gap", "1rem");

        RouterLink homeLink = new RouterLink("Home", DashboardView.class);
        navLinks.add(homeLink);

        header.add(logo, title, navLinks);
        header.getStyle().set("display", "flex").set("align-items", "center");

        return header;
    }

    private Component buildFooter() {
        Div footer = new Div();
        footer.setSizeFull();
        footer.setText("©My Stock System. Do not reuse without permission");
        footer.getStyle()
                .set("padding", "0.5rem")
                .set("border-top", "1px solid #ccc")
                .set("font-size", "small")
                .set("text-align", "center");

        return footer;
    }

    public void setBody(Component content) {
        body.removeAll();
        body.add(content);
    }
}

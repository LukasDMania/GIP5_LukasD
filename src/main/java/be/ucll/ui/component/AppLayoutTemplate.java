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

        Component header = buildHeader();
        Component footer = buildFooter();

        add(header, body, footer);
        expand(body);

        body.setSizeFull();
        body.getStyle()
                .set("overflow", "auto");
        getStyle().set("box-sizing", "border-box");
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
        header.getStyle()
                .set("width", "100%")
                .set("min-height", "3rem")
                .set("padding", "0.5rem 1rem")
                .set("border-bottom", "1px solid black")
                .set("display", "flex")
                .set("align-items", "center");

        Image logo = new Image("https://dummyimage.com/100x40/000/fff&text=MyStock", "Logo");
        logo.setHeight("2rem");

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
        footer.setText("©My Stock System. Do not reuse without permission");
        footer.getStyle()
                .set("width", "100%")
                .set("min-height", "2.5rem")
                .set("padding", "0.5rem 1rem")
                .set("border-top", "1px solid black")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");

        return footer;
    }

    public void setBody(Component content) {
        Div contentWrapper = new Div(content);
        contentWrapper.setSizeFull();
        contentWrapper.getStyle()
                .set("padding", "3rem")
                .set("box-sizing", "border-box");

        body.removeAll();
        body.add(contentWrapper);
    }
}

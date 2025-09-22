package be.ucll.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class AppLayoutTemplate extends VerticalLayout {
    private final Div body = new Div();

    public AppLayoutTemplate() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(buildHeader(), body, buildFooter());

        body.setSizeFull();
        body.getStyle().set("padding", "1rem");
    }

    private Component buildHeader() {
        Div header = new Div();
        header.setSizeFull();
        header.getStyle()
                .set("background", "#f0f0f0")
                .set("padding", "1rem")
                .set("border-bottom", "1px solid #ccc");

        //TODO: add actual site image
        Image logo = new Image("https://dummyimage.com/100x40/000/fff&text=MyShop", "Logo");
        logo.setHeight("40px");

        Span title = new Span("Order Lookup System LD");
        title.getStyle()
                .set("margin-left", "1rem")
                .set("font-weight", "bold");

        header.add(logo, title);

        return header;
    }

    private Component buildFooter() {
        Div footer = new Div();
        footer.setSizeFull();
        footer.setText("©My Order System. Do not reuse without permission");
        footer.getStyle()
                .set("background", "#f0f0f0")
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

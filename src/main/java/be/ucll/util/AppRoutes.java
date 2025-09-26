package be.ucll.util;

import com.vaadin.flow.component.Component;

public class AppRoutes {
    public static final String LOGIN_VIEW = "login";
    public static final String DASHBOARD_VIEW = "dashboard";
    public static final String PRODUCT_VIEW = "product/:id";
    public static final String PRODUCT_EDIT_VIEW = "product/edit/:id";

    private AppRoutes(){}
}

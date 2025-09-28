package be.ucll.util;

import com.vaadin.flow.component.Component;

public class AppRoutes {
    public static final String LOGIN_VIEW = "login";
    public static final String DASHBOARD_VIEW = "dashboard";
    public static final String PRODUCT_VIEW = "product/:id";
    public static final String PRODUCT_CREATE_VIEW = "product/create";
    public static final String PRODUCT_EDIT_VIEW = "product/edit/:id";
    public static final String ANALYTICSVIEW = "analytics";

    private AppRoutes(){}
}

package be.ucll.ui.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public interface ViewContractLD {
    VerticalLayout buildLayout();
    void subscribeEventListeners();
}

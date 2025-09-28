package be.ucll.ui.component.DashboardView;

import be.ucll.application.dto.SearchCriteriaDto;
import be.ucll.domain.service.ProductService;
import be.ucll.domain.service.impl.SearchHistoryService;
import be.ucll.util.UserUtil;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;

public class SearchForm extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(SearchForm.class);

    private final Binder<SearchCriteriaDto> binder = new Binder<>(SearchCriteriaDto.class);
    ComboBox<SearchCriteriaDto> historyComboBox = new ComboBox<>("Recente Zoekopdrachten");

    private final ProductService productService;
    private final SearchHistoryService searchHistoryService;

    public SearchForm(ProductService productService, SearchHistoryService searchHistoryHandler) {
        this.productService = productService;
        this.searchHistoryService = searchHistoryHandler;

        buildSearchForm();
    }

    private void buildSearchForm() {
        NumberField minAmount = new NumberField("Minimum Amount");
        NumberField maxAmount = new NumberField("Maximum Amount");
        DatePicker createdAfter = new DatePicker("Created After");
        ComboBox<String> productName = new ComboBox<>("Product name");
        productName.setAllowCustomValue(true);

        //TODO: retry pagination instead of calling full dataset
        productName.setItems(query -> {
            String filter = query.getFilter().orElse("");
            return productService.autocompleteProductNames(filter)
                    .stream()
                    .skip(query.getOffset())
                    .limit(query.getLimit());
        });

        Span errorLabel = new Span();
        errorLabel.getStyle().set("color", "red");

        historyComboBox.setItemLabelGenerator(searchHistoryService::createHistoryLabel);
        historyComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                binder.readBean(event.getValue());
                fireEvent(new SearchEvent(this, event.getValue()));
            }
        });


        binder.forField(minAmount)
                .withConverter(
                        doubleValue -> doubleValue == null ? 0 : doubleValue.intValue(),
                        intValue -> (double) intValue
                )
                .withValidator(val -> val >= 0, "Minimum stock must be positive")
                .bind(SearchCriteriaDto::getMinStock, SearchCriteriaDto::setMinStock);

        binder.forField(maxAmount)
                .withConverter(
                        doubleValue -> doubleValue == null ? 0 : doubleValue.intValue(),
                        intValue -> (double) intValue
                )
                .withValidator(val -> val >= 0, "Maximum stock must be positive")
                .bind(SearchCriteriaDto::getMaxStock, SearchCriteriaDto::setMaxStock);

        binder.forField(createdAfter)
                .withConverter(
                        localDate -> localDate == null ? null : LocalDateTime.of(localDate, LocalTime.MIN),
                        localDateTime -> localDateTime == null ? null : localDateTime.toLocalDate()
                )
                .bind(SearchCriteriaDto::getCreatedAfter, SearchCriteriaDto::setCreatedAfter);

        binder.forField(productName)
                .bind(SearchCriteriaDto::getProductName, SearchCriteriaDto::setProductName);


        Button clearButton = new Button("Clear", _ -> {
            binder.readBean(new SearchCriteriaDto());
            fireEvent(new ClearEvent(this));
            errorLabel.setText("");
        });

        Button searchButton = new Button("Search", _ -> {
            LOG.info("User triggered search");
            SearchCriteriaDto tempCriteria = new SearchCriteriaDto();

            try {
                binder.writeBean(tempCriteria);
                if (!tempCriteria.hasAtLeastOneCriteria()) {
                    LOG.warn("Search attempted with no criteria");
                    errorLabel.setText("Please enter at least one search criterion.");
                    return;
                }
                //TODO: fire search event
                try {
                    LOG.info("Firing SearchRequest Event");
                    fireEvent(new SearchEvent(this, tempCriteria));
                    LOG.info("Fired SearchResult Event");
                } catch (Exception e) {
                    LOG.error("Error publishing SearchRequestedEvent", e);
                }

                errorLabel.setText("");
            } catch (ValidationException e) {
                LOG.warn("Search form validation failed", e);
                errorLabel.setText("Please correct the fields before searching.");
            }
        });

        Button createProductButton = new Button("Create Product", _ -> {
            fireEvent(new CreateProductEvent(this));
            errorLabel.setText("");
        });

        FormLayout formLayout;
        if (userHasRole("ROLE_ADMIN") || userHasRole("ROLE_MANAGER")) {
            formLayout = new FormLayout(
                    minAmount, maxAmount, productName, createdAfter,
                    clearButton, searchButton, createProductButton, historyComboBox
            );
        } else {
            formLayout = new FormLayout(
                    minAmount, maxAmount, productName, createdAfter,
                    clearButton, searchButton, historyComboBox
            );
        }
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        VerticalLayout wrapper = new VerticalLayout(formLayout, errorLabel);
        wrapper.setAlignItems(FlexComponent.Alignment.START);
        add(wrapper);
    }

    public void setHistoryComboBoxItems(LinkedList<SearchCriteriaDto> history) {
        historyComboBox.setItems(history);
    }

    public void loadCriteria(SearchCriteriaDto criteria) {
        binder.readBean(criteria);
    }

    private boolean userHasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority(role));
    }

    public static class SearchEvent extends ComponentEvent<SearchForm> {
        private final SearchCriteriaDto criteria;
        public SearchEvent(SearchForm source, SearchCriteriaDto criteria) {
            super(source, false);
            this.criteria = criteria;
        }
        public SearchCriteriaDto getCriteria() {
            return criteria;
        }
    }

    public static class ClearEvent extends ComponentEvent<SearchForm> {
        public ClearEvent(SearchForm source) {
            super(source, false);
        }
    }
    public static class CreateProductEvent extends ComponentEvent<SearchForm> {
        public  CreateProductEvent(SearchForm source) {
            super(source, false);
        }
    }


    public <T extends ComponentEvent<?>> Registration addListener(
            Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}

package be.ucll.ui.component;

import be.ucll.application.dto.SearchCriteriaDto;
import be.ucll.domain.service.ProductService;
import be.ucll.domain.service.impl.SearchHistoryService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchForm extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(SearchForm.class);

    private final Binder<SearchCriteriaDto> binder = new Binder<>(SearchCriteriaDto.class);
    ComboBox<SearchCriteriaDto> historyComboBox = new ComboBox<>("Recente Zoekopdrachten");

    private final ProductService productService;
    private final SearchHistoryService searchHistoryHandler;

    public SearchForm(ProductService productService, SearchHistoryService searchHistoryHandler) {
        this.productService = productService;
        this.searchHistoryHandler = searchHistoryHandler;

        buildSearchForm();
    }

    private void buildSearchForm() {
        NumberField minAmount = new NumberField("Minimum Amount");
        NumberField maxAmount = new NumberField("Maximum Amount");
        ComboBox<String> productName = new ComboBox<>("Product name");
        productName.setAllowCustomValue(true);

        //TODO: retry pagination instead of calling full dataset
        productName.setItems(query -> {
            String filter = query.getFilter().orElse("");
            //TODO: add product function for autocomplete
            return productService.autocompleteProductNames(filter)
                    .stream()
                    .skip(query.getOffset())
                    .limit(query.getLimit());
        });

        Span errorLabel = new Span();
        errorLabel.getStyle().set("color", "red");

        historyComboBox.setItemLabelGenerator(searchHistoryHandler::createHistoryLabel);
        historyComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                binder.readBean(event.getValue());
                //TODO: fire search event
            }
        });

        binder.forField(productName)
                .bind(SearchCriteriaDto::getProductName, SearchCriteriaDto::setProductName);

        FormLayout formLayout = new FormLayout(
                minAmount, maxAmount, productName, createdAfter, clearButton, searchButton, historyComboBox
        );
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        VerticalLayout wrapper = new VerticalLayout(formLayout, errorLabel);
        wrapper.setAlignItems(FlexComponent.Alignment.START);
        add(wrapper);
    }
}

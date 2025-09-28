package be.ucll.ui.component;

import be.ucll.application.dto.product.ProductRequestDto;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class CreateProductForm extends VerticalLayout {

    private final Binder<ProductRequestDto> binder = new Binder<>(ProductRequestDto.class);

    private final TextField nameField = new TextField("Name");
    private final IntegerField stockField = new IntegerField("Initial Stock");
    private final TextArea descriptionField = new TextArea("Description");

    private final Span errorLabel = new Span();

    public CreateProductForm() {
        buildForm();
    }

    private void buildForm() {
        binder.forField(nameField)
                .asRequired("Name is required")
                .bind(ProductRequestDto::getName, ProductRequestDto::setName);

        binder.forField(stockField)
                .withConverter(
                        integer -> integer == null ? 0 : integer,
                        integer -> integer
                )
                .withValidator(val -> val >= 0, "Initial stock must be >= 0")
                .bind(ProductRequestDto::getInitialStock, ProductRequestDto::setInitialStock);

        binder.forField(descriptionField)
                .bind(ProductRequestDto::getDescription, ProductRequestDto::setDescription);

        //Buttons
        Button saveButton = new Button("Save", _ -> saveProduct());
        Button clearButton = new Button("Clear", _ -> clearForm());

        errorLabel.getStyle().set("color", "red");

        FormLayout formLayout = new FormLayout(nameField, stockField, descriptionField, saveButton, clearButton);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        VerticalLayout wrapper = new VerticalLayout(formLayout, errorLabel);
        wrapper.setAlignItems(FlexComponent.Alignment.START);

        add(wrapper);
    }

    private void saveProduct() {
        ProductRequestDto dto = new ProductRequestDto("", 0);
        try {
            binder.writeBean(dto);
            errorLabel.setText("");
            fireEvent(new SaveEvent(this, dto));
        } catch (ValidationException e) {
            errorLabel.setText("Please correct the errors before saving.");
        }
    }

    private void clearForm() {
        binder.readBean(new ProductRequestDto("", 0));
        errorLabel.setText("");
        fireEvent(new ClearEvent(this));
    }

    public void loadProduct(ProductRequestDto dto) {
        binder.readBean(dto);
    }

    // Events
    public static class SaveEvent extends ComponentEvent<CreateProductForm> {
        private final ProductRequestDto product;

        public SaveEvent(CreateProductForm source, ProductRequestDto product) {
            super(source, false);
            this.product = product;
        }

        public ProductRequestDto getProduct() {
            return product;
        }
    }

    public static class ClearEvent extends ComponentEvent<CreateProductForm> {
        public ClearEvent(CreateProductForm source) {
            super(source, false);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(
            Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}

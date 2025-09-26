package be.ucll.ui.component.ProductEditView;

import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.application.dto.product.ProductUpdateRequestDto;
import be.ucll.util.NotificationUtil;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

public class ProductEditForm extends VerticalLayout {

    private TextField nameField = new TextField("Name");
    private IntegerField stockField = new IntegerField("Stock");
    private TextArea descriptionField = new TextArea("Description");
    private Button saveButton = new Button("Save");
    private Button cancelButton = new Button("Cancel");

    private Long productId;

    public ProductEditForm() {
        FormLayout formLayout = new FormLayout();
        stockField.setMin(0);

        formLayout.add(nameField, stockField, descriptionField);
        add(formLayout, saveButton, cancelButton);

        saveButton.addClickListener(e -> validateAndSave());
        cancelButton.addClickListener(e -> fireEvent(new CancelEvent(this)));
    }

    public void setProduct(ProductResponseDto product) {
        this.productId = product.getId();
        nameField.setValue(product.getName() != null ? product.getName() : "");
        stockField.setValue(product.getStock());
        descriptionField.setValue(product.getDescription() != null ? product.getDescription() : "");
    }

    public ProductUpdateRequestDto toUpdateDto() {
        return new ProductUpdateRequestDto(
                productId,
                nameField.getValue(),
                stockField.getValue(),
                descriptionField.getValue()
        );
    }

    private void validateAndSave() {
        if (nameField.isEmpty()) {
            NotificationUtil.showNotification("Name cannot be empty", 2000);
            return;
        }
        if (stockField.getValue() == null || stockField.getValue() < 0) {
            NotificationUtil.showNotification("Stock cannot go below 0", 2000);
            return;
        }
        if (stockField.getValue() == null || stockField.getValue() > 1000000) {
            NotificationUtil.showNotification("Stock cannot exceed 1000000", 2000);
            return;
        }
        fireEvent(new SaveEvent(this, toUpdateDto()));
    }

    public static class SaveEvent extends ComponentEvent<ProductEditForm> {
        private final ProductUpdateRequestDto productDto;
        public SaveEvent(ProductEditForm source, ProductUpdateRequestDto productUpdateRequestDto) {
            super(source, false);
            this.productDto = productUpdateRequestDto;
        }
        public ProductUpdateRequestDto getProductUpdateRequestDto() {
            return productDto;
        }
    }

    public static class CancelEvent extends ComponentEvent<ProductEditForm> {
        public CancelEvent(ProductEditForm source) {
            super(source, false);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }
    public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
        return addListener(CancelEvent.class, listener);
    }
}

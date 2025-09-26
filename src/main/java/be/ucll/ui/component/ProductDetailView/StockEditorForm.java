package be.ucll.ui.component.ProductDetailView;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.shared.Registration;

public class StockEditorForm extends VerticalLayout {

    private final IntegerField amountField = new IntegerField("Stock Amount");

    private int currentStock;

    public StockEditorForm(int currentStock) {
        setCurrentStock(currentStock);

        amountField.setLabel("Stock Amount");
        amountField.setMin(0);

        Button increaseButton = new Button("Increase", _ -> {
            if (validateAmount(true)) {
                fireEvent(new StockAdjustEvent(this, amountField.getValue()));
                amountField.setValue(0);
            }
        });

        Button decreaseButton = new Button("Decrease", _ -> {
            if (validateAmount(false)) {
                fireEvent(new StockAdjustEvent(this, -amountField.getValue()));
                amountField.setValue(0);
            }
        });

        HorizontalLayout buttons = new HorizontalLayout(increaseButton, decreaseButton);
        add(amountField, buttons);
    }

    private boolean validateAmount(boolean isIncrease) {
        Integer v = amountField.getValue();
        if (v == null) {
            amountField.setInvalid(true);
            amountField.setErrorMessage("Please enter a number.");
            return false;
        }
        if (v < 0) {
            amountField.setInvalid(true);
            amountField.setErrorMessage("Amount cannot be negative.");
            return false;
        }
        if (isIncrease) {
            if (v > 1000000 || currentStock + v > 1000000) {
                amountField.setInvalid(true);
                amountField.setErrorMessage("Maximum allowed is 1,000,000.");
                return false;
            }
        }
        if (!isIncrease) {
            if (currentStock - v < 0) {
                amountField.setInvalid(true);
                amountField.setErrorMessage("Stock cannot go negative.");
                return false;
            }
        }

        amountField.setInvalid(false);
        return true;
    }

    public int getCurrentStock() {
        return currentStock;
    }
    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public static class StockAdjustEvent extends ComponentEvent<StockEditorForm> {
        private final int delta;
        public StockAdjustEvent(StockEditorForm source, int delta) {
            super(source, false);
            this.delta = delta;
        }
        public int getDelta() { return delta; }
    }

    public Registration addStockAdjustListener(ComponentEventListener<StockAdjustEvent> listener) {
        return addListener(StockAdjustEvent.class, listener);
    }
}

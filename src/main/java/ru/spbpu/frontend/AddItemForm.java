package ru.spbpu.frontend;

import ru.spbpu.service.StorageItem;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.List;
import java.awt.Dimension;

public class AddItemForm extends BaseApplicationForm{
    private JPanel addItemForm;
    private JComboBox componentSelector;
    private JSpinner componentAmount;
    private JButton OKButton;

    @Override
    JPanel createFormPanel() {
        return addItemForm;
    }

    @Override
    String getTitle() {
        return "Add item";
    }

    @Override
    Dimension getSize() {
        return new Dimension(400, 170);
    }

    public AddItemForm(Application app) {
        super(app);
        initItemsList();
        initOKButton();
    }

    private void initItemsList() {
        List<StorageItem> items = getService().getStorageItems();
        for(StorageItem i: items) {
            String description = String.format("%s (price: %s, %s left)", i.getName(), i.getPrice(), i.getAmount());
            componentSelector.addItem(new ComboBoxItem<Integer>(i.getId(), description));
        }
    }

    private void initOKButton() {
        OKButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                ComboBoxItem<Integer> selectedComponent = (ComboBoxItem<Integer>)componentSelector.getSelectedItem();
                Integer amount = (Integer) componentAmount.getValue();
                getService().addItemToClientOrder(selectedComponent.getValue(), amount);
                JFrame activeFrame = getApp().getActiveFrame();
                activeFrame.dispatchEvent(new WindowEvent(activeFrame, WindowEvent.WINDOW_CLOSING));
            }
        });
    }
}

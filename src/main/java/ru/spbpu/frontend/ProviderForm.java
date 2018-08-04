package ru.spbpu.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProviderForm extends BaseApplicationForm {

    private JPanel mainForm;
    private JButton toLoginButton;
    private JTabbedPane mainTabPanel;
    private JPanel componentListTab;
    private JPanel orderListTab;

    public ProviderForm(Application app) {
        super(app);
        toLoginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                switchToForm(new LoginForm(getApp()));
            }
        });
    }

    @Override
    JPanel createFormPanel() {
        return mainForm;
    }

    @Override
    String getTitle() {
        return String.format("Main form - %s", getApp().getUserName());
    }

    @Override
    Dimension getSize() {
        final int width = 500;
        final int height = 350;
        return new Dimension(width, height);
    }
}

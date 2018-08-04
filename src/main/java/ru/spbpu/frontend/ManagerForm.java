package ru.spbpu.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ManagerForm extends BaseApplicationForm{

    private JPanel managerForm;
    private JButton changeUserButton;

    public ManagerForm(Application app) {
        super(app);
        changeUserButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                getService().logout();
                switchToForm(new LoginForm(getApp()));
            }
        });
    }

    @Override
    JPanel createFormPanel() {
        return managerForm;
    }

    @Override
    String getTitle() {
        return String.format("PC assembly: (manager %s)", getService().activeUserName());
    }

    @Override
    Dimension getSize() {
        return null;
    }
}

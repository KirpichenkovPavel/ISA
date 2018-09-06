package ru.spbpu.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginForm extends BaseApplicationForm {
    private JButton loginButton;
    private JPasswordField passwordInput;
    private JPanel loginForm;
    private JTextField loginInput;
    private JComboBox roleSelector;

    public LoginForm(Application app) {
        super(app);
        initRoleSelector();
        initLoginButton();
    }

    private void initRoleSelector() {
        roleSelector.addItem(new ComboBoxItem<String>("MANAGER", "Manager"));
        roleSelector.addItem(new ComboBoxItem<String>("PROVIDER", "Provider"));
        roleSelector.addItem(new ComboBoxItem<String>("CLIENT", "Client"));
    }

    private void initLoginButton() {
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                String loginString = loginInput.getText();
                ComboBoxItem<String> selectedRole = (ComboBoxItem<String>)roleSelector.getSelectedItem();
                if (selectedRole != null && getService().login(loginString, selectedRole.getValue())) {
                    switch (selectedRole.getValue()) {
                        case "MANAGER":
                            switchToForm(new ManagerForm(getApp()));
                            break;
                        case "PROVIDER":
                            switchToForm(new ProviderForm(getApp()));
                            break;
                        case "CLIENT":
                            switchToForm(new ClientForm(getApp()));
                            break;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Login unsuccessful, try again");
                }
            }
        });
    }

    @Override
    JPanel createFormPanel() {
        return loginForm;
    }

    @Override
    String getTitle() {
        return "Log in";
    }

    @Override
    Dimension getSize() {
        return null;
    }
}

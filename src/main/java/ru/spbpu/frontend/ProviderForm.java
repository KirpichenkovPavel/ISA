package ru.spbpu.frontend;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProviderForm extends BaseApplicationForm {

    private JPanel mainForm;
    private JButton toLoginButton;
    private JTabbedPane mainTabPanel;
    private JPanel componentListTab;
    private JPanel orderListTab;
    private JTable componentsTable;

    public ProviderForm(Application app) {
        super(app);
        init();
    }

    private void init() {
        initLoginButton();
        initComponentsTable();
    }

    private void initLoginButton() {
        toLoginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                switchToForm(new LoginForm(getApp()));
            }
        });
    }

    private void initComponentsTable() {

        TableModel tableModel = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return 2;
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int i, int j) {
                return String.format("%d %d", i, j);
            }
        };
        componentsTable.setModel(tableModel);
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

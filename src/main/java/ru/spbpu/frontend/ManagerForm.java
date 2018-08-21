package ru.spbpu.frontend;

import org.javatuples.Quartet;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ManagerForm extends BaseApplicationForm{

    private JPanel managerForm;
    private JButton changeUserButton;
    private JTabbedPane tabbedPane1;
    private JTable clientOrdersTable;

    @Override
    JPanel createFormPanel() {
        return managerForm;
    }

    @Override
    String getTitle() {
        return String.format("PC assembly: user %s (manager)", getService().activeUserName());
    }

    @Override
    Dimension getSize() {
        return new Dimension(1000, 650);
    }

    public ManagerForm(Application app) {
        super(app);
        initChangeUserButton();
        initClientOrdersTable();
    }

    private void initChangeUserButton() {
        changeUserButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                getService().logout();
                switchToForm(new LoginForm(getApp()));
            }
        });
    }

    private void initClientOrdersTable() {
        List<Quartet<Integer, String, String, String>> orders = getService().getAllClientOrders();
        TableModel tableModel = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return orders.size() + 1;
            }

            @Override
            public int getColumnCount() {
                return 4;
            }

            @Override
            public Object getValueAt(int i, int j) {
                if (i == 0) {
                    switch (j) {
                        case 0: return "Order number";
                        case 1: return "Client name";
                        case 2: return "Items";
                        case 3: return "Status";
                    }
                    return "";
                } else {
                    return orders.get(i - 1).getValue(j);
                }
            }
        };
        clientOrdersTable.setModel(tableModel);
    }
}

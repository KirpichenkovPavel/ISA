package ru.spbpu.frontend;

import org.javatuples.Quartet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

public class ManagerForm extends BaseApplicationForm {

    private JPanel managerForm;
    private JButton changeUserButton;
    private JTabbedPane tabbedPane1;
    private JTable clientOrdersTable;
    private JButton cancelClientOrderButton;
    private JButton acceptClientOrderButton;
    private JButton setDoneButton;
    private JButton loadStorageButton;
    private Integer selectedClientOrderId;

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
        initCancelClientOrderButton();
        initAcceptClientOrderButton();
        initSetDoneButton();
        initLoadStorageButton();
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
        String column_names[] = {"Order number", "Client name", "Items", "Status"};
        clientOrdersTable.setModel(new DefaultTableModel(column_names, 4) {
            @Override
            public int getRowCount() {
                return orders.size();
            }
            @Override
            public Object getValueAt(int i, int j) {
                return orders.get(i).getValue(j);
            }
            @Override
            public boolean isCellEditable(int i, int j) {
                return false;
            }
        });
        clientOrdersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                int selectedRow = clientOrdersTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < clientOrdersTable.getRowCount()) {
                    selectedClientOrderId = (Integer) clientOrdersTable.getValueAt(selectedRow, 0);
                } else {
                    selectedClientOrderId = null;
                }
                if (mouseEvent.getClickCount() == 2) {
                    getApp().openForm(new OrderDetailForm(getApp(), selectedClientOrderId));
                }
            }
        });
    }

    private void initCancelClientOrderButton() {
        cancelClientOrderButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (selectedClientOrderId != null) {
                    if (getService().cancelClientOrder(selectedClientOrderId)) {
                        initClientOrdersTable();
                    } else {
                        JOptionPane.showMessageDialog(null, "Cancel unsuccessful");
                    }
                }
            }
        });
    }

    private void initAcceptClientOrderButton() {
        acceptClientOrderButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (selectedClientOrderId != null) {
                    if (getService().acceptClientOrder(selectedClientOrderId)) {
                        initClientOrdersTable();
                    } else {
                        JOptionPane.showMessageDialog(null, "Accept was unsuccessful");
                    }
                }
            }
        });
    }

    private void initSetDoneButton() {
        setDoneButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (selectedClientOrderId != null) {
                    if (getService().executeClientOrder(selectedClientOrderId)) {
                        initClientOrdersTable();
                    } else {
                        JOptionPane.showMessageDialog(null, "Operation was unsuccessful");
                    }
                }
            }
        });
    }

    private void initLoadStorageButton(){
        loadStorageButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                JFileChooser fileChooser = new JFileChooser();
                int chooseResult = fileChooser.showOpenDialog(managerForm);
                if (chooseResult == JFileChooser.APPROVE_OPTION) {
                    Optional<String> errorMessage = getService().loadStorage(fileChooser.getSelectedFile());
                    errorMessage.ifPresent(s -> JOptionPane.showMessageDialog(null, s));
                }
            }
        });
    }
}

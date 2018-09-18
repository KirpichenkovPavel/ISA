package ru.spbpu.frontend;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
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

    private void initLoadStorageButton() {
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        managerForm = new JPanel();
        managerForm.setLayout(new GridLayoutManager(4, 3, new Insets(10, 10, 10, 10), -1, -1));
        changeUserButton = new JButton();
        changeUserButton.setText("Change user");
        managerForm.add(changeUserButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        managerForm.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        tabbedPane1 = new JTabbedPane();
        managerForm.add(tabbedPane1, new GridConstraints(1, 0, 3, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Client Orders", panel1);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(0, 1, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        clientOrdersTable = new JTable();
        scrollPane1.setViewportView(clientOrdersTable);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 1, new Insets(5, 5, 5, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cancelClientOrderButton = new JButton();
        cancelClientOrderButton.setText("Cancel");
        panel2.add(cancelClientOrderButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        acceptClientOrderButton = new JButton();
        acceptClientOrderButton.setText("Accept");
        panel2.add(acceptClientOrderButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setDoneButton = new JButton();
        setDoneButton.setText("Set done");
        panel2.add(setDoneButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loadStorageButton = new JButton();
        loadStorageButton.setText("Restock");
        managerForm.add(loadStorageButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return managerForm;
    }
}

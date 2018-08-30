package ru.spbpu.frontend;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import ru.spbpu.service.StorageItem;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;

public class ClientForm extends BaseApplicationForm{

    private JPanel clientForm;
    private JButton changeUserButton;
    private JTabbedPane userTabs;
    private JPanel ordersTab;
    private JPanel makeOrderTab;
    private JTable ordersTable;
    private JButton btnCreateOrder;
    private JTable newOrderTable;
    private JButton deleteItemButton;
    private JButton addItemButton;
    private JButton submitButton;
    private JButton discardButton;
    private JTable clientPaymentsTable;
    private JButton makePaymentButton;
    private JButton cancelButton;
    private JTable completeOrdersTable;
    private JButton acceptButton;
    private JButton rejectButton;
    private String selectedItemName;
    private Map<String, Integer> priceMap;
    private Integer selectedPaymentOrderId;
    private Integer selectedCompleteOrderId;

    @Override
    JPanel createFormPanel() {
        return clientForm;
    }

    @Override
    String getTitle() {
        return String.format("PC assembly: user %s (client)", getService().activeUserName());
    }

    @Override
    Dimension getSize() {
        return new Dimension(1000, 650);
    }

    public ClientForm(Application app) {
        super(app);
        priceMap = getService().getStoragePrices();
        initChangeUserButton();
        initOrdersTable();
        initAddItemButton();
        initNewOrderTable();
        initRemoveItemButton();
        initDiscardButton();
        initSubmitButton();
        initClientPaymentsTable();
        initMakePaymentButton();
        initCancelOrderButton();
        initCompleteOrdersTable();
        initRejectButton();
        initAcceptButton();
        initTabs();
    }

    private void initializationSwitch(int tabIndex) {
        switch (tabIndex) {
            case 0:
                initOrdersTable();
                break;
            case 1:
                initNewOrderTable();
                break;
            case 2:
                initClientPaymentsTable();
                break;
            case 3:
                initCompleteOrdersTable();
                break;
            default:
                System.out.println(tabIndex);
        }
    }

    private void initTabs() {
        userTabs.addChangeListener(changeEvent -> {
            JTabbedPane pane = (JTabbedPane) changeEvent.getSource();
            int tabIndex = pane.getSelectedIndex();
            initializationSwitch(tabIndex);
        });
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

    private void initOrdersTable() {
        List<Triplet<Integer, String, String>> clientOrders = getService().getActiveClientOrdersList();
        TableModel tableModel = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return clientOrders.size() + 1;
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public Object getValueAt(int i, int j) {
                if (i == 0) {
                    switch (j) {
                        case 0: return "Order number";
                        case 1: return "Items";
                        case 2: return "Status";
                    }
                    return "";
                } else {
                    Triplet<Integer, String, String> row = clientOrders.get(i - 1);
                    return row.getValue(j);
                }
            }
        };
        ordersTable.setModel(tableModel);
        ordersTable.setTableHeader(null);
    }

    private void initAddItemButton() {
        addItemButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                Application app = getApp();
                app.openForm(new AddItemForm(app));
            }
        });
    }

    private void initNewOrderTable() {
        List<StorageItem> newOrderItems = getService().getNewOrderItems();
        newOrderTable.setModel(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return newOrderItems.size() + 1;
            }

            @Override
            public int getColumnCount() {
                return 4;
            }

            @Override
            public Object getValueAt(int i, int j) {
                if (i == 0) {
                    switch (j){
                        case 0: return "Name";
                        case 1: return "Amount";
                        case 2: return "Price";
                        case 3: return "Cost";
                    }
                } else {
                    String name = newOrderItems.get(i - 1).getName();
                    int amount = newOrderItems.get(i - 1).getAmount();
                    int price = priceMap.getOrDefault(name, -1);
                    long cost = price >= 0 ? price * amount : -1;
                    switch (j) {
                        case 0: return name;
                        case 1: return amount;
                        case 2: return price >= 0 ? price : "N/A";
                        case 3: return cost >= 0 ? cost : "N/A";
                    }
                }
                return null;
            }
        });
        newOrderTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                int selectedRow = newOrderTable.getSelectedRow();
                if (selectedRow > 0 && selectedRow < newOrderTable.getRowCount()) {
                    selectedItemName = newOrderTable.getValueAt(selectedRow, 0).toString();
                } else {
                    selectedItemName = null;
                }
            }
        });
    }

    private void initRemoveItemButton() {
        deleteItemButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (selectedItemName != null) {
                    getService().removeComponentFromNewOrder(selectedItemName);
                    selectedItemName = null;
                    initNewOrderTable();
                }
            }
        });
    }

    private void initDiscardButton() {
        discardButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                getService().discardClientOrder();
                initNewOrderTable();
            }
        });
    }

    private void initSubmitButton() {
        submitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                getService().submitClientOrder();
                initOrdersTable();
                initNewOrderTable();
            }
        });
    }

    @Override
    public void updateForm() {
        initNewOrderTable();
    }

    private void initClientPaymentsTable() {
        List<Triplet<Integer, Integer, String>> payments = getService().getActiveClientPayments();
        clientPaymentsTable.setModel(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return payments.size() + 1;
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public Object getValueAt(int i, int j) {
                if (i == 0) {
                    switch (j){
                        case 0: return "Order number";
                        case 1: return "Amount";
                        case 2: return "Payment status";
                    }
                } else {
                    return payments.get(i - 1).getValue(j);
                }
                return null;
            }
        });
        clientPaymentsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                int selectedRow = clientPaymentsTable.getSelectedRow();
                if (selectedRow > 0 && selectedRow < clientPaymentsTable.getRowCount()) {
                    selectedPaymentOrderId = (Integer) clientPaymentsTable.getValueAt(selectedRow, 0);
                } else {
                    selectedPaymentOrderId = null;
                }
            }
        });
        clientPaymentsTable.setTableHeader(null);
    }

    private void initMakePaymentButton() {
        makePaymentButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (selectedPaymentOrderId != null) {
                    getService().makePayment(selectedPaymentOrderId);
                    initClientPaymentsTable();
                    initOrdersTable();
                }
            }
        });
    }

    private void initCancelOrderButton() {
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (selectedPaymentOrderId != null) {
                    getService().cancelOrderByClient(selectedPaymentOrderId);
                    initClientPaymentsTable();
                    initOrdersTable();
                }
            }
        });
    }

    private void initCompleteOrdersTable() {
        List<Pair<Integer, String>> orders = getService().getCompleteClientOrders();
        completeOrdersTable.setModel(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return orders.size() + 1;
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int i, int j) {
                if (i == 0) {
                    switch (j){
                        case 0: return "Order number";
                        case 1: return "Items";
                    }
                } else {
                    return orders.get(i - 1).getValue(j);
                }
                return null;
            }
        });
        completeOrdersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                int selectedRow = completeOrdersTable.getSelectedRow();
                if (selectedRow > 0 && selectedRow < completeOrdersTable.getRowCount()) {
                    selectedCompleteOrderId = (Integer) completeOrdersTable.getValueAt(selectedRow, 0);
                } else {
                    selectedCompleteOrderId = null;
                }
            }
        });
        completeOrdersTable.setTableHeader(null);
    }

    private void initRejectButton() {
        rejectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (selectedCompleteOrderId != null) {
                    getService().cancelOrderByClient(selectedCompleteOrderId);
                    initCompleteOrdersTable();
                    initClientPaymentsTable();
                    initOrdersTable();
                }
            }
        });
    }

    private void initAcceptButton() {
        acceptButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (selectedCompleteOrderId != null) {
                    getService().acceptCompleteOrder(selectedCompleteOrderId);
                    initCompleteOrdersTable();
                    initClientPaymentsTable();
                    initOrdersTable();
                }
            }
        });
    }
}
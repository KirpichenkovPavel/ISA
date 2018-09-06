package ru.spbpu.frontend;

import org.javatuples.Quintet;
import org.javatuples.Triplet;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class OrderDetailForm extends BaseApplicationForm {
    private JPanel orderDetailPanel;
    private JTable itemsTable;
    private JLabel orderNumber;
    private JLabel totalCost;
    private JLabel clientName;
    private JLabel orderStatus;
    private Integer orderId;

    @Override
    JPanel createFormPanel() {
        return orderDetailPanel;
    }

    @Override
    String getTitle() {
        return "Order information";
    }

    @Override
    Dimension getSize() {
        return new Dimension(600, 300);
    }

    public OrderDetailForm(Application app, Integer orderId) {
        super(app);
        this.orderId = orderId;
        initComponents();
    }

    private void initComponents() {
        Quintet<Integer, List<Triplet<String, Integer, Integer>>, Integer, String, String> info =
                getService().getOrderDetailInfo(orderId);
        orderNumber.setText(info.getValue0().toString());
        totalCost.setText(info.getValue2().toString());
        clientName.setText(info.getValue3());
        orderStatus.setText(info.getValue4());
        List<Triplet<String, Integer, Integer>> itemInfo = info.getValue1();
        String column_names[] = {"Component name", "Amount", "Price"};
        itemsTable.setModel(new DefaultTableModel(column_names, 3) {
            @Override
            public int getRowCount() {
                return itemInfo.size();
            }
            @Override
            public Object getValueAt(int i, int j) {
                return itemInfo.get(i).getValue(j);
            }
            @Override
            public boolean isCellEditable(int i, int j) {
                return false;
            }
        });
    }
}

package ru.spbpu.data;

import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.logic.*;
import ru.spbpu.util.Pair;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderMapper extends BasicMapper implements OrderAccessor {

    public OrderMapper(String url, AccessorRegistry registry) {
        super(url, registry);
    }

    @Override
    Entity parseResultSetEntry(ResultSet resultSet) throws ApplicationException {
        try {
            int id = resultSet.getInt("id");
            String status = resultSet.getString("status");
            Integer from_id = resultSet.getInt("from_id");
            Integer to_id = resultSet.getInt("to_id");
            Integer payment_id = resultSet.getInt("payment_id");
            UserAccessor userAccessor = (UserAccessor) getRegistry().getAccessor(AccessorRegistry.RegistryKey.USER);
            PaymentAccessor paymentAccessor = (PaymentAccessor) getRegistry().getAccessor(AccessorRegistry.RegistryKey.PAYMENT);
            BaseUser from = (BaseUser) userAccessor.getById(from_id);
            BaseUser to = (BaseUser) userAccessor.getById(to_id);
            Payment payment = (Payment) paymentAccessor.getById(payment_id);
            Order order = new Order(getRegistry());
            order.setFrom(from);
            order.setTo(to);
            order.setPayment(payment);
            order.setStatus(Order.OrderStatus.valueOf(status));
            return order;
        } catch (SQLException e) {
            throw new ApplicationException(String.format("SQL exception: %s", e.getMessage()));
        }
    }

    @Override
    Map<String, Object> getDatabaseFields(Entity entity) {
        Map<String, Object> fieldMap = new HashMap<>();
        Order order = (Order) entity;
        BaseUser from = order.getFrom();
        Integer fromId = from == null ? null : from.getId();
        fieldMap.put("from_id", fromId);
        BaseUser to = order.getTo();
        Integer toId = to == null ? null : to.getId();
        fieldMap.put("to_id", toId);
        fieldMap.put("status", order.getStatus().name());
        Payment payment = order.getPayment();
        Integer paymentId = payment == null ? null : payment.getId();
        fieldMap.put("payment_id", paymentId);
        return fieldMap;
    }

    @Override
    public Order getById(int id) throws ApplicationException {
        Order order = (Order) super.getById(id);
        return order;
    }

    @Override
    String getTableNameBase() {
        return "order";
    }

    @Override
    public List<Order> getOrdersByUser(User user) {
        return null;
    }

    @Override
    public List<Order> getOrdersByTargetUser(User user) {
        return null;
    }

    @Override
    Map<String, Pair<List<? extends Entity>, BasicMapper>> getM2MFields(Entity entity) {
        Map<String, Pair<List<? extends Entity>, BasicMapper>> m2mFields = new HashMap<>();
        Order order = (Order) entity;
        BasicMapper itemMapper = (ItemMapper) getRegistry().getAccessor(AccessorRegistry.RegistryKey.ITEM);
        m2mFields.put("items", new Pair<>(order.getItems(), itemMapper));
        return m2mFields;
    }
}

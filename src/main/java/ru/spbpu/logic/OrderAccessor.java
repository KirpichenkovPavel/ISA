package ru.spbpu.logic;

import java.util.List;

public interface OrderAccessor extends Accessor {
    Order getOrder(int id);
    List<Order> getOrdersByUser(User user);
    List<Order> getOrdersByTargetUser(User user);
    void addOrder(Order newOrder);
    void saveOrder(Order changedOrder);
}

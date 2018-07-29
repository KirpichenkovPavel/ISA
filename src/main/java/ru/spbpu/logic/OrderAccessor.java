package ru.spbpu.logic;

import java.util.List;

public interface OrderAccessor extends Accessor {
    List<Order> getOrdersByUser(User user);
    List<Order> getOrdersByTargetUser(User user);
}

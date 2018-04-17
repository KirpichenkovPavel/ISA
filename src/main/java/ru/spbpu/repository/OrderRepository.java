package ru.spbpu.repository;

import ru.spbpu.assembly.Order;
import ru.spbpu.user.User;

import java.util.List;

public class OrderRepository implements OrderAccessor {

    @Override
    public Order getOrder(int id) {
        return null;
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
    public void addOrder(Order newOrder) {

    }

    @Override
    public void saveOrder(Order changedOrder) {

    }

    @Override
    public Order getById(int id) {
        return null;
    }

    @Override
    public List<AbstractStorableObject> getAll() {
        return null;
    }

    @Override
    public void saveObject(AbstractStorableObject object) {

    }

    @Override
    public void updateObject(AbstractStorableObject object) {

    }
}

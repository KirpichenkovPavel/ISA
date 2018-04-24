package ru.spbpu.data;

import ru.spbpu.logic.Order;
import ru.spbpu.logic.Entity;
import ru.spbpu.logic.OrderAccessor;
import ru.spbpu.logic.User;

import java.util.List;

public class OrderRepository extends AbstractRepository implements OrderAccessor {

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
    public List<Entity> getAll() {
        return null;
    }

    @Override
    public void saveObject(Entity object) {

    }

    @Override
    public void updateObject(Entity object) {

    }
}

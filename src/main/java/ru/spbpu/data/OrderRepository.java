package ru.spbpu.data;

import ru.spbpu.logic.*;

import java.util.List;

public class OrderRepository extends AbstractRepository implements OrderAccessor {

    @Override
    public List<Order> getOrdersByUser(User user) {
        return null;
    }

    @Override
    public List<Order> getOrdersByTargetUser(User user) {
        return null;
    }


    @Override
    public Order getById(int id) {
        return null;
    }

    @Override
    public List<Entity> getAll() {
        return null;
    }

//    @Override
//    public void saveObject(Entity object) {
//
//    }
//
//    @Override
//    public void updateObject(Entity object) {
//
//    }

    @Override
    public AccessorRegistry getRegistry() {
        return null;
    }
}

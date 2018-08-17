package ru.spbpu.data;

import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.logic.*;

import java.util.List;

public class OrderRepository extends AbstractRepository implements OrderAccessor {

    @Override
    public Order getById(int id) {
        return null;
    }

    @Override
    public List<Entity> getAll() {
        return null;
    }

    @Override
    public AccessorRegistry getRegistry() {
        return null;
    }

    @Override
    public List<ClientOrder> getOrdersByClient(Client client) throws ApplicationException {
        return null;
    }

    @Override
    public List<Order> getOrdersBySourceUser(BaseUser user) throws ApplicationException {
        return null;
    }

    @Override
    public List<Order> getOrdersByTargetUser(BaseUser user) throws ApplicationException {
        return null;
    }
}

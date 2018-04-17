package ru.spbpu.user;

import ru.spbpu.assembly.Component;
import ru.spbpu.assembly.Item;
import ru.spbpu.assembly.Order;
import ru.spbpu.assembly.Storage;
import ru.spbpu.exceptions.ApplicationException;

public class Client extends AbstractUser implements User {

    public Client(String name) {
        super(name);
    }

    @Override
    public Role getRole() {
        return Role.CLIENT;
    }

    public void makeOrder() {
        Order newOrder = new Order(this);
        newOrder.create();
    }

    public void submitOrder(Order order, Storage storage) throws ApplicationException {
        if (order.canBeSubmitted(storage)) {
            order.submit(storage);
            order.update();
        }
    }

    public void addItemToOrder(Component component, Order order, int amount) throws ApplicationException{
        Item newItem = order.addItem(component, amount);
        newItem.create();
        order.update();
    }

    public void changeComponentAmountInOrder(Component component, Order order, int amount) throws ApplicationException {
        order.changeAmount(component, amount);
        order.update();
    }

    public void markOrderAsDone(Order order) throws ApplicationException {
        order.close();
        order.update();
    }


}

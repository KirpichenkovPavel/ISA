package ru.spbpu.logic;

import ru.spbpu.exceptions.ApplicationException;

public class Client extends AbstractUser implements User {

    Client(String name, AccessorRegistry registry) {
        super(name, registry);
    }

    @Override
    public Role getRole() {
        return Role.CLIENT;
    }

    public Order makeOrder() {
        Order newOrder = this.getRegistry().newOrder(this);
        newOrder.create();
        return newOrder;
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

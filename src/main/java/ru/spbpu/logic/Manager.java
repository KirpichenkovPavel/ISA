package ru.spbpu.logic;

import ru.spbpu.exceptions.ApplicationException;

public class Manager extends AbstractUser implements User {

    Manager(String name, AccessorRegistry registry ){
        super(name, registry);
    }

    @Override
    public Role getRole() {
        return Role.MANAGER;
    }

    public void acceptOrder(Order order, Storage storage) throws ApplicationException{
        if (order.canBeAccepted(storage)) {
            order.accept(storage);
            Payment payment = order.addPayment(this);
            order.update();
            payment.create();
        }
    }

    public void executeOrder(Order order) throws ApplicationException {
        order.execute();
        order.update();
    }
}
package ru.spbpu.user;

import ru.spbpu.assembly.Order;
import ru.spbpu.assembly.Payment;
import ru.spbpu.assembly.Storage;
import ru.spbpu.exceptions.ApplicationException;

public class Manager extends AbstractUser implements User {

    public Manager(String name){
        super(name);
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
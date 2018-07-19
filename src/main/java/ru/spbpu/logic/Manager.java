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

    public void acceptOrder(ClientOrder order) throws ApplicationException{
        Storage storage = getRegistry().getStorage();
        if (!order.canBeAccepted()){
            throw new ApplicationException("Order can't be accepted due to status or storage state",
                    ApplicationException.Type.STORAGE);
        }
        for (Item itemFromOrder: order.getItems()) {
            storage.takeComponents(itemFromOrder.getComponent(), itemFromOrder.getAmount());
        }
        Payment payment = getRegistry().newPayment(order.getClient(), this, order.totalPrice());
        payment.create();
        order.setStatus(Order.OrderStatus.ACCEPTED);
        order.setPayment(payment);
        order.update();

    }

    public void setPrice(Component component, int price) throws ApplicationException {
        Storage storage = getRegistry().getStorage();
        if (storage.componentExists(component)) {
            storage.setPrice(component, price);
        }
        storage.update();
    }

    public void executeOrder(ClientOrder order) throws ApplicationException {
        if (!(order.getStatus() == Order.OrderStatus.PAID))
            throw new ApplicationException("Order is not paid", ApplicationException.Type.ORDER_STATUS);
        order.setStatus(Order.OrderStatus.DONE);
        order.update();
    }

    public void cancelOrder(ClientOrder order) throws ApplicationException {
        switch (order.getStatus()) {
            case SUBMITTED:
                order.setStatus(Order.OrderStatus.CANCELED);
                break;
            case ACCEPTED:
                order.returnItemsToStorage();
                order.setStatus(Order.OrderStatus.CANCELED);
                break;
            case PAID:
            case DONE:
                order.cancelPayment();
                order.returnItemsToStorage();
                order.setStatus(Order.OrderStatus.CANCELED);
                break;
            default:
                throw new ApplicationException("Can't cancel order in this status", ApplicationException.Type.ORDER_STATUS);
        }
    }

    public WholesaleOrder makeNewWholesaleOrder(Provider provider) throws ApplicationException {
        WholesaleOrder order = getRegistry().newWholesaleOrder(this, provider);
        order.create();
        return order;
    }

    public void addItemToOrder(WholesaleOrder order, Component component, int amount) {

    }
}
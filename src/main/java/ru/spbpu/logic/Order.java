package ru.spbpu.logic;

import ru.spbpu.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Order extends Entity {

    public enum OrderStatus {
        NEW,
        SUBMITTED,
        ACCEPTED,
        PAID,
        DONE,
        CLOSED,
        CANCELED
    }

    private OrderStatus status;
    private List<Item> items;
    private Payment payment;

    Order (AccessorRegistry registry) {
        super(registry);
        this.status = OrderStatus.NEW;
        this.items = new ArrayList<>();
    }

    @Override
    protected AccessorRegistry.RegistryKey accessorRegistryKey() {
        return AccessorRegistry.RegistryKey.ORDER;
    }

    void setStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    protected void setItems(List<Item> newItems) {
        items = newItems;
    }

    protected void setPayment(Payment payment) {
        this.payment = payment;
    }

    OrderStatus getStatus() {return status;}

    void addItem(Item newItem) {
        for (Item i: items) {
            if (i.getComponent().equals(newItem.getComponent())){
                i.setAmount(i.getAmount() + newItem.getAmount());
                return;
            }
        }
        items.add(newItem);
    }

    void changeAmount(Component component, int newAmount) {
        items = items.stream()
                .peek(item -> {
                    if (item.getComponent().equals(component))
                        item.setAmount(newAmount);
                })
                .collect(Collectors.toList());
    }

    abstract boolean canBeSubmitted();

    abstract boolean canBeAccepted();

    boolean canBePaid() {
        return payment != null && status == OrderStatus.ACCEPTED;
    }

    boolean canBeDone() {
        return payment != null
                && payment.getStatus() == Payment.PaymentStatus.COMPLETE
                && status == OrderStatus.PAID;
    }

    boolean canBeClosed() {
        return status == OrderStatus.DONE;
    }

    boolean canBeCancelled() {
        return status != OrderStatus.CLOSED && status != OrderStatus.CANCELED;
    }

    int totalPrice() {
        int total = 0;
        for (Item item: items) {
            total += item.getPrice() * item.getAmount();
        }
        return total;
    }

    List<Item> getItems() {
        return items;
    }

    Payment getPayment() {
        return payment;
    }

    void returnItemsToStorage() throws ApplicationException{
        Storage storage = getRegistry().getStorage();
        for (Item i: items) {
            storage.addItem(i);
        }
        storage.update();
    }

    void cancelPayment() throws ApplicationException {
        if (payment != null){
            payment.cancel();
            payment.update();
        }
    }
}









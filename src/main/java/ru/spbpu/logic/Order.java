package ru.spbpu.logic;

import ru.spbpu.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Order extends Entity {

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
    private BaseUser from;
    private BaseUser to;

    public Order (AccessorRegistry registry) {
        super(registry);
        this.status = OrderStatus.NEW;
        this.items = new ArrayList<>();
    }

    public BaseUser getFrom() {
        return from;
    }

    public BaseUser getTo() {
        return to;
    }

    public void setFrom(BaseUser from) {
        this.from = from;
    }

    public void setTo(BaseUser to) {
        this.to = to;
    }

    public Payment getPayment() {
        return this.payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    protected AccessorRegistry.RegistryKey accessorRegistryKey() {
        return AccessorRegistry.RegistryKey.ORDER;
    }

    public OrderStatus getStatus() {return status;}

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

    boolean canBeSubmitted() {
        return true;
    };

    boolean canBeAccepted() {
        return true;
    };

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









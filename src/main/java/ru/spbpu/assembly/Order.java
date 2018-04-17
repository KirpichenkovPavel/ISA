package ru.spbpu.assembly;

import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.repository.*;
import ru.spbpu.user.Client;
import ru.spbpu.user.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Order extends AbstractStorableObject {

    public enum OrderStatus {
        NEW,
        SUBMITTED,
        ACCEPTED,
        PAID,
        DONE,
        CLOSED,
        CANCELED
    }

    private int id;
    private Client client;
    private OrderStatus status;
    private List<Item> items;
    private Payment payment;

    public Order(Client _client) {
        super();
        this.client = _client;
        this.status = OrderStatus.NEW;
    }

    public void setStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    public int getId() {return id;}
    public Client getClient() {return client;}
    public OrderStatus getStatus() {return status;}

    public Item addItem(Component component, int amount) throws ApplicationException {
        Item newItem = new Item(component, amount);
        items.add(newItem);
        return newItem;
    }

    public void changeAmount(Component component, int newAmount) throws ApplicationException {
        items = items.stream()
                .peek(item -> {
                    if (item.getComponent().equals(component))
                        item.setAmount(newAmount);
                })
                .collect(Collectors.toList());
    }

    public boolean canBeSubmitted(Storage storage) throws ApplicationException {
        for (Item item: items) {
            if (!storage.componentExists(item.getComponent()))
                throw new ApplicationException();
        }
        return true;
    }

    public void submit(Storage storage) throws ApplicationException {
        List<Item> itemsWithPrice = new ArrayList<>();
        for (Item itemInOrder: items) {
            Component component = itemInOrder.getComponent();
            itemsWithPrice.add(new Item(component, itemInOrder.getAmount(), storage.componentPrice(component)));
        }
        items = itemsWithPrice;
        setStatus(OrderStatus.SUBMITTED);
    }

    public boolean canBeAccepted (Storage storage) {
        if (getStatus() != OrderStatus.SUBMITTED)
            return false;
        for (Item item: items) {
            if (storage.componentAmount(item.getComponent()) < item.getAmount())
                return false;
        }
        return true;
    }

    public void accept(Storage storage) throws ApplicationException {
        if (!canBeAccepted(storage)){
            throw new ApplicationException();
        }
        for (Item itemFromOrder: items) {
            storage.takeComponents(itemFromOrder.getComponent(), itemFromOrder.getAmount());
        }
        setStatus(OrderStatus.ACCEPTED);
    }

    public int totalPrice() {
        int total = 0;
        for (Item item: items) {
            total += item.getPrice();
        }
        return total;
    }

    public Payment addPayment(Manager manager) throws ApplicationException{
        if (!(getStatus() == OrderStatus.ACCEPTED))
            throw new ApplicationException();
        payment = new Payment(client, manager, totalPrice());
        return payment;
    }

    public void execute() throws ApplicationException {
        if (!(getStatus() == OrderStatus.PAID))
            throw new ApplicationException();
        status = OrderStatus.DONE;
    }

    public void close() throws ApplicationException {
        if (!(getStatus() == OrderStatus.DONE))
            throw new ApplicationException();
        status = OrderStatus.CLOSED;
    }

    @Override
    protected Accessor getAccessor() {
        return new OrderRepository();
    }
}









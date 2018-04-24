package ru.spbpu.logic;

import ru.spbpu.data.OrderRepository;
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

    private int id;
    private Client client;
    private OrderStatus status;
    private List<Item> items;
    private Payment payment;

    Order(Client _client, AccessorRegistry registry) {
        super(registry);
        this.client = _client;
        this.status = OrderStatus.NEW;
        this.items = new ArrayList<>();
    }

    @Override
    protected AccessorRegistry.RegistryKey accessorRegistryKey() {
        return AccessorRegistry.RegistryKey.ORDER;
    }

    public void setStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }



    public int getId() {return id;}
    public Client getClient() {return client;}
    public OrderStatus getStatus() {return status;}

    public Item addItem(Component component, int amount) throws ApplicationException {
        Item newItem = this.getRegistry().newItem(component, amount);
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
            itemsWithPrice.add(this.getRegistry().newItem(component, itemInOrder.getAmount(), storage.componentPrice(component)));
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
        payment = getRegistry().newPayment(client, manager, totalPrice());
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

    public List<Item> getItems() {
        return items;
    }
}









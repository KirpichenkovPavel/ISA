package ru.spbpu.assembly;

import ru.spbpu.user.Client;

import java.util.ArrayList;

public class Order {

    public enum OrderStatus {
        NEW,
        SUBMITTED,
        CHECKED,
        PAID,
        DONE,
        CANCELED
    }

    private int id;
    private Client client;
    private OrderStatus status;
    private ArrayList<Item> items;
    private Payment payment;

    public Order(int _id, Client _client) {
        this.id = id;
        this.client = _client;
        this.status = OrderStatus.NEW;
    }

    public void setStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    public int getId() {return id;}
    public Client getClient() {return client;}
    public OrderStatus getStatus() {return status;}

    public void addItem(Component component, int amount) {
        items.add(new Item(component, amount));
    }


}









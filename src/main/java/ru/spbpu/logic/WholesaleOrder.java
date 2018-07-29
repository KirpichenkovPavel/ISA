package ru.spbpu.logic;

public class WholesaleOrder extends Order {

    WholesaleOrder(Manager manager, Provider to, AccessorRegistry registry) {
        super(registry);
        this.setFrom(manager);
        this.setTo(to);
    }

    public Manager getManager() {
        return (Manager) getFrom();
    }

    public Provider getProvider() {
        return (Provider) getTo();
    }

    @Override
    boolean canBeSubmitted() {
        return (getStatus() == OrderStatus.NEW);
    }

    @Override
    boolean canBeAccepted() {
        return (getStatus() == OrderStatus.SUBMITTED);
    }
}

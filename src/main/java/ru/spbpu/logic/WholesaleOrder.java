package ru.spbpu.logic;

public class WholesaleOrder extends Order {

    private Manager manager;
    private Provider provider;

    WholesaleOrder(Manager manager, Provider to, AccessorRegistry registry) {
        super(registry);
        this.manager = manager;
        this.provider = to;
    }

    public Manager getManager() {
        return manager;
    }

    public Provider getProvider() {
        return provider;
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

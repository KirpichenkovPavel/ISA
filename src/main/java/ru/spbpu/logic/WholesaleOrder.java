package ru.spbpu.logic;

public class WholesaleOrder extends Order {

    public WholesaleOrder(BaseUser manager, BaseUser provider, AccessorRegistry registry) {
        super(registry);
        this.setFrom(manager);
        this.setTo(provider);
    }

    public WholesaleOrder(BaseUser manager, BaseUser provider, AccessorRegistry registry, int id) {
        super(registry, id);
        this.setFrom(manager);
        this.setTo(provider);
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

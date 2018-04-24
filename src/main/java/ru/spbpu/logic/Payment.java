package ru.spbpu.logic;

import ru.spbpu.data.ItemRepository;

public class Payment extends Entity {

    private int id;
    private User from;
    private User to;
    private int amount;
    private PaymentStatus status;

    public enum PaymentStatus {
        OPEN,
        COMPLETE,
        CLOSED,
        CANCELED
    }

    Payment(User from, User to, int amount, AccessorRegistry registry) {
        super(registry);
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.status = PaymentStatus.OPEN;
    }

    @Override
    protected AccessorRegistry.RegistryKey accessorRegistryKey() {
        return AccessorRegistry.RegistryKey.PAYMENT;
    }

    public void setPaid() {
        this.status = PaymentStatus.COMPLETE;
    }

    public void close() {
        this.status = PaymentStatus.CLOSED;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELED;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public User getSourceUser() {
        return from;
    }

    public User getTargetUser() {
        return to;
    }


    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }
}

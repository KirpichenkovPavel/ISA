package ru.spbpu.logic;

public class Payment extends Entity {

    private BaseUser from;
    private BaseUser to;
    private int amount;
    private PaymentStatus status;

    public enum PaymentStatus {
        OPEN,
        COMPLETE,
        CLOSED,
        CANCELED
    }

    Payment(BaseUser from, BaseUser to, int amount, AccessorRegistry registry) {
        super(registry);
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.status = PaymentStatus.OPEN;
    }

    public Payment(BaseUser from, BaseUser to, int amount, PaymentStatus status, int id, AccessorRegistry registry) {
        super(registry);
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.status = status;
        this.setId(id);
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

    public BaseUser getSourceUser() {
        return from;
    }

    public BaseUser getTargetUser() {
        return to;
    }

    public int getAmount() {
        return amount;
    }
}

package ru.spbpu.assembly;

import ru.spbpu.repository.Accessor;
import ru.spbpu.repository.AbstractStorableObject;
import ru.spbpu.repository.ItemRepository;
import ru.spbpu.user.User;

public class Payment extends AbstractStorableObject{

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

    public Payment(User from, User to, int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.status = PaymentStatus.OPEN;
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

    @Override
    protected Accessor getAccessor() {
        return new ItemRepository();
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }
}

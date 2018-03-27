package ru.spbpu.assembly;

import ru.spbpu.user.UserInterface;

public class Payment {

    private int id;
    private UserInterface from;
    private UserInterface to;
    private int amount;
    private PaymentStatus status;

    public enum PaymentStatus {
        OPEN,
        COMPLETE,
        CLOSED,
        CANCELED
    }

    public Payment(UserInterface from, UserInterface to, int amount) {
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

    public UserInterface getSourceUser() {
        return from;
    }

    public UserInterface getTargetUser() {
        return to;
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }
}

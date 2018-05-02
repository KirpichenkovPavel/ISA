package ru.spbpu.logic;

public class ClientOrder extends Order {

    private Client client;

    ClientOrder(Client from, AccessorRegistry registry) {
        super(registry);
        this.client = from;
    }

    Client getClient() {
        return client;
    }

    @Override
    boolean canBeSubmitted(){
        Storage storage = getRegistry().getStorage();
        for (Item item: this.getItems()) {
            if (!storage.componentExists(item.getComponent()))
                return false;
        }
        return true;
    }

    @Override
    boolean canBeAccepted () {
        Storage storage = getRegistry().getStorage();
        if (getStatus() != OrderStatus.SUBMITTED)
            return false;
        for (Item item: getItems()) {
            if (storage.componentAmount(item.getComponent()) < item.getAmount())
                return false;
        }
        return true;
    }


}

package ru.spbpu.logic;

import ru.spbpu.exceptions.ApplicationException;

import java.util.HashMap;
import java.util.Map;

public class AccessorRegistry {

    private Map<RegistryKey, Accessor> registry;

    public enum RegistryKey {
        ITEM,
        COMPONENT,
        USER,
        STORAGE,
        ORDER,
        PAYMENT,
    }

    public AccessorRegistry(){}

    public void setUp(ItemAccessor ia, ComponentAccessor ca, UserAccessor ua, StorageAccessor sa,
                      OrderAccessor oa, PaymentAccessor pa) {
        registry = new HashMap<>();
        registry.put(RegistryKey.ITEM, ia);
        registry.put(RegistryKey.COMPONENT, ca);
        registry.put(RegistryKey.USER, ua);
        registry.put(RegistryKey.STORAGE, sa);
        registry.put(RegistryKey.ORDER, oa);
        registry.put(RegistryKey.PAYMENT, pa);
    }

    public Accessor getAccessor(RegistryKey key) {
        return registry.get(key);
    }

    public Item newItem(Component component, int amount, int price) {
        return new Item(component, amount, price, this);
    }

    public Item newItem(Component component, int amount) {
        return newItem(component, amount, 0);
    }

    public ClientOrder newOrder(Client from) {
        return new ClientOrder(from,this);
    }

    public WholesaleOrder newWholesaleOrder(Manager from, Provider to) {
        return new WholesaleOrder(from, to, this);
    }

    public Payment newPayment(User from, User to, int amount) {
        return new Payment(from, to, amount, this);
    }

    public User newUser(String name, User.Role role) throws ApplicationException {
        switch (role) {
            case CLIENT:
                return new Client(name, this);
            case MANAGER:
                return new Manager(name, this);
            case PROVIDER:
                return new Provider(name, this);
            default:
                throw new ApplicationException();
        }
    }

    public Component newComponent(String name) {
        return new Component(name, this);
    }

    public Storage getStorage() {
        return Storage.getInstance(this);
    }
}

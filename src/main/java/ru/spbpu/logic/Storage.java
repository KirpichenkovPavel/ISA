package ru.spbpu.logic;

import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.data.StorageRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Storage extends Entity {
    private Map<Component, Item> components;

    Storage(AccessorRegistry registry) {
        super(registry);
        components = new HashMap<>();
    }

    @Override
    protected AccessorRegistry.RegistryKey accessorRegistryKey() {
        return AccessorRegistry.RegistryKey.STORAGE;
    }

    public void addItem(Item newItem) {
        if (components.containsKey(newItem.getComponent())){
            Item componentsInStorage = components.get(newItem.getComponent());
            componentsInStorage.setAmount(componentsInStorage.getAmount() + newItem.getAmount());
        }
        else {
            components.put(newItem.getComponent(), newItem);
        }
    }

    public boolean componentExists(Component component) {
        return components.containsKey(component);
    }

    public int componentAmount(Component component) {
        return componentExists(component) ? components.get(component).getAmount() : 0;
    }

    public int componentPrice(Component component) throws ApplicationException {
        if (!componentExists(component))
            throw new ApplicationException();
        return components.get(component).getPrice();
    }

    public void takeComponents(Component component, int amount) throws ApplicationException {
        int reserve = componentAmount(component);
        if (reserve < amount){
            throw new ApplicationException();
        }
        components.get(component).setAmount(reserve - amount);
    }

    public void setPrice(Component component, int newPrice) {
        if (components.containsKey(component)) {
            components.get(component).setPrice(newPrice);
        }
        else {
            components.put(component, this.getRegistry().newItem(component, newPrice, 0));
        }
    }

    public void removeFromStorage(Component component) throws ApplicationException {
        if (!components.containsKey(component)){
            throw new ApplicationException();
        }
        components.remove(component);
    }
}

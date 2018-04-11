package ru.spbpu.assembly;

import ru.spbpu.exceptions.StorageException;
import ru.spbpu.repository.StorageAccessor;

import java.util.HashMap;
import java.util.Map;

public class Storage {
    private Map<Component, Item> components;

    private Storage() {
        components = new HashMap<Component, Item>();
    }

    public Storage loadStorage(StorageAccessor dataLayerStorageAccessor) {
        Storage newStorage = new Storage();
        newStorage.components = dataLayerStorageAccessor.getComponents();
        return newStorage;
    }

    public void saveStorageChanges(StorageAccessor dataLayerStorageAccessor) {
        dataLayerStorageAccessor.saveStorage(this.components);
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

    public int componentAmount(Component component) {
        return components.containsKey(component) ? components.get(component).getAmount() : 0;
    }

    public Item takeComponents(Component component, int amount) throws StorageException {
        int reserve = componentAmount(component);
        if (reserve < amount){
            throw new StorageException();
        }
        components.get(component).setAmount(reserve - amount);
        return new Item(component, components.get(component).getPrice(), amount);
    }

    public void setPrice(Component component, int newPrice) {
        if (components.containsKey(component)) {
            components.get(component).setPrice(newPrice);
        }
        else {
            components.put(component, new Item(component, newPrice, 0));
        }
    }

    public void removeFromStorage(Component component) throws StorageException{
        if (!components.containsKey(component)){
            throw new StorageException();
        }
        components.remove(component);
    }
}

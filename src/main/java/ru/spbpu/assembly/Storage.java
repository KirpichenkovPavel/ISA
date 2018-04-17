package ru.spbpu.assembly;

import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.repository.Accessor;
import ru.spbpu.repository.AbstractStorableObject;
import ru.spbpu.repository.StorageAccessor;
import ru.spbpu.repository.StorageRepository;

import java.util.HashMap;
import java.util.Map;

public class Storage extends AbstractStorableObject {
    private Map<Component, Item> components;

    private Storage() {
        components = new HashMap<Component, Item>();
    }

    @Override
    protected Accessor getAccessor() {
        return new StorageRepository();
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
//        return new Item(component, components.get(component).getPrice(), amount);
    }

    public void setPrice(Component component, int newPrice) {
        if (components.containsKey(component)) {
            components.get(component).setPrice(newPrice);
        }
        else {
            components.put(component, new Item(component, newPrice, 0));
        }
    }

    public void removeFromStorage(Component component) throws ApplicationException {
        if (!components.containsKey(component)){
            throw new ApplicationException();
        }
        components.remove(component);
    }
}

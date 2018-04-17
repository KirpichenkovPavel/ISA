package ru.spbpu.repository;

import ru.spbpu.assembly.Component;
import ru.spbpu.assembly.Item;
import ru.spbpu.assembly.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageRepository extends AbstractRepository implements StorageAccessor{

    private Map<Component, Item> components;

    public StorageRepository() {
        components = new HashMap<>();
    }

    public void addItem(String name, int amount, int price, ComponentRepository cRep) {
        Component component = cRep.getByName(name);
        if (component != null)
            components.put(component, new Item(component, amount, price));
    }

    public Map<Component, Item> getComponents() {
        return new HashMap<>(components);
    }

    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        items.addAll(components.values());
        return items;
    }

    public void saveStorage(Map<Component, Item> components) {
        this.components = new HashMap<>(components);
    }

    @Override
    public Storage getById(int id) {
        return (Storage)super.getById(id);
    }

    @Override
    public List<Storage> getAll() {
        return (List<Storage>) super.getAll();
    }
}

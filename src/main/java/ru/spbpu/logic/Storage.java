package ru.spbpu.logic;

import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.exceptions.ApplicationException.Type;

import java.util.*;
import java.util.stream.Collectors;

public class Storage extends Entity {
    private List<Item> items;
    private String name;

    public Storage(AccessorRegistry registry) {
        super(registry);
        items = new ArrayList<>();
        this.name = "Storage";
    }

    public Storage(AccessorRegistry registry, String name, int id) {
        super(registry, id);
        items = new ArrayList<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Item> getItems() {
        return items;
    }

    @Override
    protected Class accessorRegistryKey() {
        return Storage.class;
    }

    public boolean componentExists(Component component) {
        return items.stream().anyMatch(item -> item.getComponent().equals(component));
    }

    private Item getItemByComponent(Component component) {
        for (Item i: items) {
            if (i.getComponent().equals(component))
                return i;
        }
        return null;
//        Optional<Item> match = items.stream().filter(item -> item.getComponent().equals(component)).findFirst();
//        return match.orElse(null);
    }

    private void updateItem(Item newItem) {
        List<Item> filteredItems = items.stream()
                .filter(item -> !item.getComponent().equals(newItem.getComponent()))
                .collect(Collectors.toList());
        filteredItems.add(newItem);
        items = filteredItems;
    }

    public void addItem(Item newItem) {
        if (componentExists(newItem.getComponent())){
            Item componentsInStorage = getItemByComponent(newItem.getComponent());
            componentsInStorage.setAmount(componentsInStorage.getAmount() + newItem.getAmount());
        }
        else {
            items.add(newItem);
        }
    }

    public int componentAmount(Component component) {
        Item item = getItemByComponent(component);
        return item != null ? item.getAmount() : 0;
    }

    public int componentPrice(Component component) throws ApplicationException {
        Item item = getItemByComponent(component);
        if (item == null)
            throw new ApplicationException("Component does not exist", Type.STORAGE);
        return item.getPrice();
    }

    public void takeComponents(Component component, int amount) throws ApplicationException {
        int reserve = componentAmount(component);
        if (reserve < amount){
            throw new ApplicationException("Not enough components in storage", Type.STORAGE);
        }
        getItemByComponent(component).setAmount(reserve - amount);
    }

    public void setPrice(Component component, int newPrice) throws ApplicationException {
        Item item = getItemByComponent(component);
        if (item != null) {
            item.setPrice(newPrice);
            item.update();
        } else {
            Item newItem = this.getRegistry().newItem(component, 0, newPrice);
            newItem.create();
            items.add(newItem);
        }
    }

    public void removeFromStorage(Component component) throws ApplicationException {
        if (componentExists(component)){
            throw new ApplicationException("Component does not exist", Type.STORAGE);
        }
        items = items.stream().filter(item -> item.getComponent() != component).collect(Collectors.toList());
    }
}

package ru.spbpu.repository;

import ru.spbpu.assembly.Component;
import ru.spbpu.assembly.Item;

import java.util.List;
import java.util.Map;

public interface StorageAccessor {

    public void addItem(String name, int amount, int price, ComponentRepository cRep);

    public Map<Component, Item> getComponents();

    public List<Item> getItems();

    public void saveStorage(Map<Component, Item> components);
}

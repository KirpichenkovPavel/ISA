package ru.spbpu.repository;

import ru.spbpu.assembly.Component;
import ru.spbpu.assembly.Item;

import java.util.List;
import java.util.Map;

public interface StorageAccessor extends Accessor {

    void addItem(String name, int amount, int price, ComponentRepository cRep);

    Map<Component, Item> getComponents();

    List<Item> getItems();

    void saveStorage(Map<Component, Item> components);
}

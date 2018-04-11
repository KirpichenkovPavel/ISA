package ru.spbpu.repository;

import ru.spbpu.assembly.Component;

import java.util.List;

public interface ComponentAccessor {

    public void addComponent(Component component);

    public Component getByName(String name);

    public List<Component> getAll();

}

package ru.spbpu.repository;

import ru.spbpu.assembly.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentRepository extends AbstractRepository implements ComponentAccessor{

    private Map<String, Component> components;

    public ComponentRepository() {
        components = new HashMap<String, Component>();
    }

    public void addComponent(Component component) {
        components.put(component.getName(), component);
    }

    public Component getByName(String name) {
        return components.get(name);
    }

    public List<Component> getAll() {
        ArrayList<Component> result = new ArrayList<Component>();
        result.addAll(components.values());
        return result;
    }

}

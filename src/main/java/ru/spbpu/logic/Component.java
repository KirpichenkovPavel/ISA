package ru.spbpu.logic;

import ru.spbpu.data.ComponentRepository;

import java.util.Optional;

public class Component extends Entity {
    private String name;

    Component(String name, AccessorRegistry registry) {
        super(registry);
        this.setName(name);
    }

    public Component(String name, AccessorRegistry registry, int id) {
        super(registry);
        this.name = name;
        this.setId(id);
    }

    @Override
    protected AccessorRegistry.RegistryKey accessorRegistryKey() {
        return AccessorRegistry.RegistryKey.COMPONENT;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Component another) {
        return another.getName().equals(this.name);
    }

}

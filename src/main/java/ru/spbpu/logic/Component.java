package ru.spbpu.logic;

import ru.spbpu.data.ComponentRepository;

import java.util.Optional;

public class Component extends Entity {
    private String name;

    Component(String name, AccessorRegistry registry) {
        super(registry);
        this.name = name;

    }

    @Override
    protected AccessorRegistry.RegistryKey accessorRegistryKey() {
        return AccessorRegistry.RegistryKey.COMPONENT;
    }

    public String getName(){
        return name;
    }

    public boolean equals(Component another) {
        return another.getName().equals(this.name);
    }

}

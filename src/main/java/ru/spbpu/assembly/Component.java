package ru.spbpu.assembly;

import ru.spbpu.repository.Accessor;
import ru.spbpu.repository.AbstractStorableObject;
import ru.spbpu.repository.ComponentRepository;

public class Component extends AbstractStorableObject{
    private String name;

    Component(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public boolean equals(Component another) {
        return another.getName().equals(this.name);
    }

    @Override
    protected Accessor getAccessor() {
        return new ComponentRepository();
    }
}

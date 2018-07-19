package ru.spbpu.logic;

import ru.spbpu.exceptions.ApplicationException;

public abstract class Entity {

    private int id;
    private AccessorRegistry registry;
    private String AccessorRegistryKey;

    protected Entity(AccessorRegistry registry) {
        this.registry = registry;
    }

    protected abstract AccessorRegistry.RegistryKey accessorRegistryKey();

    protected Accessor getAccessor() {
        return registry.getAccessor(accessorRegistryKey());
    }

    protected AccessorRegistry getRegistry() {
        return registry;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void create() throws ApplicationException {
        id = getAccessor().saveObject(this);
    }

    public void update() throws ApplicationException {
        getAccessor().updateObject(this);
    }

    public Entity read() throws ApplicationException {
        return getAccessor().getById(id);
    }


}

package ru.spbpu.logic;

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

    public void create() {
        id = getAccessor().generateId();
        getAccessor().saveObject(this);
    }

    public void update() {
        getAccessor().updateObject(this);
    }

    public Entity read() {
        return getAccessor().getById(id);
    }


}

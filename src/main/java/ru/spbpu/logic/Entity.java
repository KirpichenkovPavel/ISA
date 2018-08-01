package ru.spbpu.logic;

import ru.spbpu.exceptions.ApplicationException;

import java.lang.reflect.Field;
import java.util.List;

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
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void create() throws ApplicationException {
        this.id = getAccessor().saveObject(this);
    }

    public void update() throws ApplicationException {
        getAccessor().updateObject(this);
    }

    public Entity read() throws ApplicationException {
        return getAccessor().getById(id);
    }

    public void setField(String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(this, value);
    }
}
